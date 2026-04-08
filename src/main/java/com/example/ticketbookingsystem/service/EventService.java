package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateEventRequest;
import com.example.ticketbookingsystem.dto.EventResponse;
import com.example.ticketbookingsystem.dto.UpdateEventRequest;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.exception.VenueCapacityExceededException;
import com.example.ticketbookingsystem.mapper.EventMapper;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.Venue;
import com.example.ticketbookingsystem.repository.EventRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.repository.VenueRepository;
import com.example.ticketbookingsystem.security.AuthenticationHelper;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final TicketRepository ticketRepository;
    private final AuthenticationHelper authenticationHelper;
    private final EventMapper eventMapper;

    public EventService(EventRepository eventRepository, VenueRepository venueRepository, TicketRepository ticketRepository, AuthenticationHelper authenticationHelper, EventMapper eventMapper){
        this.eventRepository = eventRepository;
        this.venueRepository = venueRepository;
        this.ticketRepository = ticketRepository;
        this.authenticationHelper = authenticationHelper;
        this.eventMapper = eventMapper;
    }

    public EventResponse createEvent(CreateEventRequest request){

        authenticationHelper.requireAdminOrOrganizer();

        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        if(request.getTotalCapacity() > venue.getMaxCapacity()){
            throw new VenueCapacityExceededException(
                    String.format("Event capacity %d exceeds venue maximum capacity %d for venue ID: %d",
                            request.getTotalCapacity(),
                            venue.getMaxCapacity(),
                            venue.getId())
            );
        }

            Event event = eventMapper.mapToEvent(request, venue);
            event.setOwner(authenticationHelper.getCurrentUser());

            eventRepository.save(event);

            return eventMapper.mapToResponse(event);
    }

    public Page<EventResponse> getEvents(Pageable pageable){
        authenticationHelper.requireAdminOrOrganizerOrUser();
        Page<Event> eventPage = eventRepository.findAll(pageable);
        return eventPage.map(eventMapper::mapToResponse);
    }

    public Page<EventResponse> getMyEvents(Pageable pageable){
        authenticationHelper.requireAdminOrOrganizer();
        Page<Event> eventPage = eventRepository.findByOwner(authenticationHelper.getCurrentUser(), pageable);
        return eventPage.map(eventMapper::mapToResponse);
    }

    public EventResponse getEvent(Long eventId){

        authenticationHelper.requireAdminOrOrganizerOrUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        return eventMapper.mapToResponse(event);
    }

    public EventResponse updateEvent(Long eventId, UpdateEventRequest request){

        authenticationHelper.requireAdminOrOrganizer();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if(!authenticationHelper.isAdmin() && !event.getOwner().getId().equals(authenticationHelper.getCurrentUser().getId())){
            throw new UnauthorizedActionException("Access denied: you can only update your own events");
        }

        Venue venue = event.getVenue();

        if (request.getVenueId() != null) {
            venue = venueRepository.findById(request.getVenueId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));
        }

        if(request.getTotalCapacity() != null && request.getTotalCapacity() > venue.getMaxCapacity()){
            throw new VenueCapacityExceededException(
                    String.format("Event capacity %d exceeds venue maximum capacity %d for venue ID: %d",
                            request.getTotalCapacity(),
                            venue.getMaxCapacity(),
                            venue.getId())
            );
        }

        eventMapper.mapUpdateToEvent(event, request, venue);
        eventRepository.save(event);

        return eventMapper.mapToResponse(event);
    }

    @Transactional
    public void deleteEvent(Long eventId){

        authenticationHelper.requireAdminOrOrganizer();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if(!authenticationHelper.isAdmin() &&
                !event.getOwner().getId().equals(authenticationHelper.getCurrentUser().getId())){
            throw new UnauthorizedActionException("Action denied: you can only delete your own events");
        }

        List<Ticket> tickets = ticketRepository.findByEvent(event);

        for (Ticket ticket : tickets) {
            if (ticket.getBooking() != null &&
                    ticket.getBooking().getStatus() != BookingStatus.CANCELLED) {
                throw new IllegalStateException("Cannot delete event with active bookings");
            }
        }

        ticketRepository.deleteAll(tickets);
        eventRepository.delete(event);
    }



}
