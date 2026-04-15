package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateEventRequest;
import com.example.ticketbookingsystem.dto.EventResponse;
import com.example.ticketbookingsystem.dto.UpdateEventRequest;
import com.example.ticketbookingsystem.enums.EventCategory;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.exception.VenueCapacityExceededException;
import com.example.ticketbookingsystem.mapper.EventMapper;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.model.Venue;
import com.example.ticketbookingsystem.repository.EventRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.repository.VenueRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private VenueRepository venueRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private SecurityContextService securityContextService;
    @Mock private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private User owner;
    private Venue venue1;
    private Venue venue2;
    private Event event;
    private EventResponse eventResponse;
    private Ticket ticket1;
    private Ticket ticket2;
    private List<Ticket> tickets = new ArrayList<>();

    @BeforeEach
    void setUp() {
        owner = new User("Adam", "adam@test.com", "Password", UserRole.ORGANIZER);
        owner.setId(1L);

        venue1 = new Venue("Toyota Centre", "Houston, TX", "Address", 3000);
        venue1.setId(1L);

        venue2 = new Venue("Google Park", "San Fransisco, CA", "Address", 500);
        venue2.setId(2L);

        event = new Event();
        event.setOwner(owner);

        eventResponse = new EventResponse(1L, "NBA Playoffs", "Lakers vs Rockets",1L,
                "adam@test.com", EventCategory.SPORTS, 3000, 1L,
                "Toyota Centre", LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(4));

        ticket1 = new Ticket(event, TicketType.STANDARD, 250L, TicketStatus.AVAILABLE);
        ticket2 = new Ticket(event, TicketType.VIP, 500L, TicketStatus.AVAILABLE);

        tickets.add(ticket1);
        tickets.add(ticket2);
    }

    //---------------------- SUCCESS ----------------------

    @Test
    void createEvent() {
        CreateEventRequest request = new CreateEventRequest("NBA Playoffs", "Lakers vs Rockets",
                EventCategory.SPORTS, 2500, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(securityContextService.getCurrentUser()).thenReturn(owner);

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue1));
        when(eventMapper.mapToEvent(request, venue1)).thenReturn(event);

        EventResponse response = mock(EventResponse.class);
        when(eventMapper.mapToResponse(event)).thenReturn(response);

        EventResponse result = eventService.createEvent(request);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrOrganizer();
        verify(venueRepository).findById(1L);
        verify(eventRepository).save(event);
        verify(eventMapper).mapToResponse(event);
    }

    @Test
    void getEvents() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        Page<Event> eventPage = new PageImpl<>(List.of(event));
        Pageable pageable = PageRequest.of(0, 10);

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);
        when(eventMapper.mapToResponse(event)).thenReturn(eventResponse);

        Page<EventResponse> result = eventService.getEvents(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(eventResponse.getId(), result.getContent().get(0).getId());
        assertEquals(eventResponse.getName(), result.getContent().get(0).getName());

        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository).findAll(pageable);
        verify(eventMapper).mapToResponse(event);
    }

    @Test
    void getMyEvents() {
        Page<Event> eventPage = new PageImpl<>(List.of(event));
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(securityContextService.getCurrentUser()).thenReturn(owner);
        when(eventRepository.findByOwner(owner, pageable)).thenReturn(eventPage);
        when(eventMapper.mapToResponse(event)).thenReturn(eventResponse);

        Page<EventResponse> result = eventService.getMyEvents(pageable);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrOrganizer();
        verify(eventRepository).findByOwner(owner, pageable);
        verify(eventMapper).mapToResponse(event);
    }

    @Test
    void getEvent() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventMapper.mapToResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.getEvent(1L);

        assertNotNull(result);
        assertEquals(eventResponse.getId(), result.getId());
        assertEquals(eventResponse.getName(), result.getName());
        assertEquals(eventResponse.getDescription(), result.getDescription());

        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository).findById(1L);
        verify(eventMapper).mapToResponse(event);
    }

    @Test
    void updateEvent() {
        UpdateEventRequest request = new UpdateEventRequest("Google Conference", "IT",
                EventCategory.CONFERENCE, 50, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityContextService.getCurrentUser()).thenReturn(owner);
        when(venueRepository.findById(2L)).thenReturn(Optional.of(venue2));
        when(eventMapper.mapToResponse(event)).thenReturn(eventResponse);

        EventResponse result = eventService.updateEvent(1L, request);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrOrganizer();
        verify(eventMapper).mapUpdateToEvent(event, request, venue2);
        verify(eventRepository).save(event);
        verify(eventMapper).mapToResponse(event);
    }

    @Test
    void deleteEvent() {
        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityContextService.getCurrentUser()).thenReturn(owner);
        when(ticketRepository.findByEvent(event)).thenReturn(tickets);

        eventService.deleteEvent(1L);

        verify(securityContextService).requireAdminOrOrganizer();
        verify(ticketRepository).deleteAll(tickets);
        verify(eventRepository).delete(event);
    }

    //---------------------- FAILURES ----------------------

    //CREATE EVENT

    @Test
    void createEvent_venueNotFound() {
        CreateEventRequest request = new CreateEventRequest(
                "Real Madrid vs Barcelona", "La Liga", EventCategory.SPORTS, 5000,
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.createEvent(request));

        verify(eventRepository, never()).save(any());
        verify(eventMapper, never()).mapToEvent(any(), any());
        verify(eventMapper, never()).mapToResponse(any());
    }

    @Test
    void createEvent_notAuthorized() {
        CreateEventRequest request = new CreateEventRequest(
                "Real Madrid vs Barcelona", "La Liga", EventCategory.SPORTS, 5000,
                1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2));

        doThrow(new UnauthorizedActionException("This action requires ADMIN or ORGANIZER role"))
                .when(securityContextService).requireAdminOrOrganizer();

        assertThrows(UnauthorizedActionException.class,
                        () -> eventService.createEvent(request));

        verify(eventRepository, never()).save(any());
        verify(eventMapper, never()).mapToEvent(any(), any());
        verify(eventMapper, never()).mapToResponse(any());
    }

    @Test
    void createEvent_capacityExceeded() {
        CreateEventRequest request = new CreateEventRequest("Event", "Description", EventCategory.EXHIBITION,
                3500, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(5));

        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue1));

        assertThrows(VenueCapacityExceededException.class,
                () -> eventService.createEvent(request));

        verify(eventRepository, never()).save(any());
        verify(eventMapper, never()).mapToEvent(any(), any());
        verify(eventMapper, never()).mapToResponse(any());
    }


    //GET EVENTS

    @Test
    void getEvents_notAuthorized() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("This action requires an authorized user"))
                .when(securityContextService).requireAdminOrOrganizerOrUser();

        assertThrows(UnauthorizedActionException.class,
                () -> eventService.getEvents(pageable));

        verify(eventRepository, never()).findAll();
        verify(eventMapper, never()).mapToResponse(any());
    }


    //GET MY EVENTS

    @Test
    void getMyEvents_notAuthorized() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("This action requires ADMIN or ORGANIZER role"))
                .when(securityContextService).requireAdminOrOrganizer();

        assertThrows(UnauthorizedActionException.class,
                () -> eventService.getMyEvents(pageable));

        verify(eventRepository, never()).findByOwner(any(), any());
        verify(eventMapper, never()).mapToResponse(any());
    }


    //GET EVENT

    @Test
    void getEvent_notFound() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.getEvent(1L));

        verify(eventMapper, never()).mapToResponse(any());
    }


    //UPDATE EVENT

    @Test
    void updateEvent_eventNotFound() {
        UpdateEventRequest request = new UpdateEventRequest(
                "Event", "Description", EventCategory.WORKSHOP,
                50, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.updateEvent(1L, request));

        verify(eventRepository).findById(1L);
        verify(eventRepository, never()).save(any());
        verify(eventMapper, never()).mapUpdateToEvent(any(), any(), any());
    }

    @Test
    void updateEvent_venueNotFound() {
        UpdateEventRequest request = new UpdateEventRequest(
                "Name", "Desc", EventCategory.CONFERENCE,
                50, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityContextService.getCurrentUser()).thenReturn(owner);
        when(venueRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.updateEvent(1L, request));

        verify(eventRepository, never()).save(any());
    }

    @Test
    void updateEvent_notOwner() {
        UpdateEventRequest request = new UpdateEventRequest(
                "Name", "Desc", EventCategory.CONFERENCE,
                50, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        User user = new User("John", "john@test.com", "pass", UserRole.ORGANIZER);
        user.setId(2L);

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityContextService.getCurrentUser()).thenReturn(user);

        assertThrows(UnauthorizedActionException.class,
                () -> eventService.updateEvent(1L, request));

        verify(eventRepository, never()).save(any());
        verify(eventMapper, never()).mapUpdateToEvent(any(), any(), any());
    }

    @Test
    void updateEvent_capacityExceeded() {
        UpdateEventRequest request = new UpdateEventRequest(
                "Name", "Desc", EventCategory.CONFERENCE,
                1000, 2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1));

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(securityContextService.getCurrentUser()).thenReturn(owner);
        when(venueRepository.findById(2L)).thenReturn(Optional.of(venue2));

        assertThrows(VenueCapacityExceededException.class,
                () -> eventService.updateEvent(1L, request));

        verify(eventRepository, never()).save(any());
    }


    //DELETE EVENT

    @Test
    void deleteEvent_eventNotFound() {
        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.deleteEvent(1L));

        verify(ticketRepository, never()).deleteAll();
        verify(eventRepository, never()).delete(any());
    }

    @Test
    void deleteEvent_eventNotAuthorized() {
        doThrow(new UnauthorizedActionException("This action requires ADMIN or ORGANIZER role"))
                .when(securityContextService).requireAdminOrOrganizer();

        assertThrows(UnauthorizedActionException.class,
                () -> eventService.deleteEvent(1L));

        verify(ticketRepository, never()).deleteAll();
        verify(eventRepository, never()).delete(any());
    }
}
