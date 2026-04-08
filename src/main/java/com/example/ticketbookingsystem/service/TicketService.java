package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateTicketRequest;
import com.example.ticketbookingsystem.dto.TicketResponse;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.TicketMapper;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.repository.EventRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.security.AuthenticationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;
    private final EventRepository eventRepository;
    private final TicketMapper ticketMapper;
    private final AuthenticationHelper authenticationHelper;

    public TicketService(TicketRepository ticketRepository, EventRepository eventRepository, TicketMapper ticketMapper, AuthenticationHelper authenticationHelper){
        this.ticketRepository = ticketRepository;
        this.eventRepository = eventRepository;
        this.ticketMapper = ticketMapper;
        this.authenticationHelper = authenticationHelper;
    }

    public TicketResponse generateTicket(CreateTicketRequest request){

        authenticationHelper.requireAdminOrOrganizer();

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if(!authenticationHelper.isAdmin() && !event.getOwner().getId().equals(authenticationHelper.getCurrentUser().getId())){
            throw new UnauthorizedActionException("You can only generate tickets for your own event");
        }

        Ticket ticket = ticketMapper.mapToTicket(request, event);
        ticketRepository.save(ticket);

        return ticketMapper.mapToResponse(ticket);
    }


    public Page<TicketResponse> viewAvailableTickets(Long eventId, Pageable pageable){

        authenticationHelper.requireAdminOrOrganizerOrUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Page<Ticket> ticketPage = ticketRepository.findByEventAndStatus(event, TicketStatus.AVAILABLE, pageable);

        return ticketPage.map(ticketMapper::mapToResponse);
    }


    public Page<TicketResponse> viewTicketsByEvent(Long eventId, Pageable pageable){

        authenticationHelper.requireAdminOrOrganizerOrUser();

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        Page<Ticket> ticketPage = ticketRepository.findByEvent(event, pageable);

        return ticketPage.map(ticketMapper::mapToResponse);

    }
}
