package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateTicketRequest;
import com.example.ticketbookingsystem.dto.TicketResponse;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.TicketMapper;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.EventRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
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
public class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private EventRepository eventRepository;
    @Mock private TicketMapper ticketMapper;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private TicketService ticketService;

    private User owner;
    private Event event;
    private Ticket ticket;
    private CreateTicketRequest request;
    private TicketResponse ticketResponse;

    @BeforeEach
    void setUp() {
        owner = new User("John", "john@test.com", "Password", UserRole.ORGANIZER);
        owner.setId(1L);

        event = new Event();
        event.setId(1L);
        event.setOwner(owner);

        ticket = new Ticket(event, TicketType.STANDARD, 100L, TicketStatus.AVAILABLE);

        request = new CreateTicketRequest(1L, TicketType.STANDARD, 100L);

        ticketResponse = new TicketResponse(5L, 1L, "Event", TicketType.STANDARD, 100L,
                TicketStatus.AVAILABLE, "Venue", "Address", LocalDateTime.now(), LocalDateTime.now().plusHours(2));
    }

    //---------------------- SUCCESS ----------------------

    @Test
    void generateTicket() {
        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(securityContextService.isAdmin()).thenReturn(true);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketMapper.mapToTicket(request, event)).thenReturn(ticket);
        when(ticketMapper.mapToResponse(ticket)).thenReturn(ticketResponse);

        TicketResponse result = ticketService.generateTicket(request);

        assertNotNull(result);
        assertEquals(ticketResponse.getId(), result.getId());
        assertEquals(ticketResponse.getPrice(), result.getPrice());

        verify(securityContextService).requireAdminOrOrganizer();
        verify(eventRepository).findById(1L);
        verify(ticketMapper).mapToTicket(request, event);
        verify(ticketMapper).mapToResponse(ticket);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void viewAvailableTickets() {
        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketRepository.findByEventAndStatus(event, TicketStatus.AVAILABLE, pageable)).thenReturn(ticketPage);
        when(ticketMapper.mapToResponse(ticket)).thenReturn(ticketResponse);

        Page<TicketResponse> result = ticketService.viewAvailableTickets(1L, pageable);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository).findById(1L);
        verify(ticketRepository).findByEventAndStatus(event, TicketStatus.AVAILABLE, pageable);
        verify(ticketMapper).mapToResponse(ticket);
    }

    @Test
    void viewTicketsByEvent() {
        Page<Ticket> ticketPage = new PageImpl<>(List.of(ticket));
        Pageable pageable = PageRequest.of(0,10);

        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketRepository.findByEvent(event, pageable)).thenReturn(ticketPage);
        when(ticketMapper.mapToResponse(ticket)).thenReturn(ticketResponse);

        Page<TicketResponse> result = ticketService.viewTicketsByEvent(1L, pageable);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository).findById(1L);
        verify(ticketRepository).findByEvent(event, pageable);
        verify(ticketMapper).mapToResponse(ticket);
    }

    //---------------------- FAILURES ----------------------

    //GENERATE TICKET

    @Test
    void generateTicket_eventNotFound() {
        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.generateTicket(request));

        verify(securityContextService).requireAdminOrOrganizer();
        verify(eventRepository).findById(1L);
        verify(ticketMapper, never()).mapToTicket(any(), any());
        verify(ticketMapper, never()).mapToResponse(any());
        verify(ticketRepository, never()).save(any());
    }


    @Test
    void generateTicket_notEventOwner() {
        User other = new User("Tom", "tom@test.com", "Password", UserRole.ORGANIZER);
        other.setId(2L);

        doNothing().when(securityContextService).requireAdminOrOrganizer();
        when(securityContextService.isAdmin()).thenReturn(false);
        when(securityContextService.getCurrentUser()).thenReturn(other);
        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(UnauthorizedActionException.class,
                () -> ticketService.generateTicket(request));

        verify(securityContextService).requireAdminOrOrganizer();
        verify(eventRepository).findById(1L);
        verify(ticketMapper, never()).mapToTicket(any(), any());
        verify(ticketMapper, never()).mapToResponse(any());
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void generateTicket_notAuthorized() {
        doThrow(new UnauthorizedActionException("This action requires ADMIN or ORGANIZER role"))
                .when(securityContextService).requireAdminOrOrganizer();

        assertThrows(UnauthorizedActionException.class,
                () -> ticketService.generateTicket(request));

        verify(securityContextService).requireAdminOrOrganizer();
        verify(eventRepository, never()).findById(any());
        verify(ticketMapper, never()).mapToTicket(any(), any());
        verify(ticketMapper, never()).mapToResponse(any());
        verify(ticketRepository, never()).save(any());
    }


    //VIEW AVAILABLE TICKETS

    @Test
    void viewAvailableTickets_eventNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.viewAvailableTickets(1L, pageable));

        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository).findById(1L);
        verify(ticketRepository, never()).findByEventAndStatus(any(), any(), any());
        verify(ticketMapper, never()).mapToResponse(any());
    }

    @Test
    void viewAvailableTickets_notAuthorized() {
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("Unauthorized"))
                .when(securityContextService).requireAdminOrOrganizerOrUser();

        assertThrows(UnauthorizedActionException.class,
                () -> ticketService.viewAvailableTickets(1L, pageable));

        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository, never()).findById(any());
    }


    //VIEW TICKETS BY EVENT

    @Test
    void viewTicketsByEvent_eventNotFound() {
        Pageable pageable = PageRequest.of(0,10);

        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.viewTicketsByEvent(1L, pageable));

        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository).findById(1L);
        verify(ticketRepository, never()).findByEvent(event, pageable);
        verify(ticketMapper, never()).mapToResponse(ticket);
    }

    @Test
    void viewTicketsByEvent_notAuthorized() {
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("Unauthorized"))
                .when(securityContextService).requireAdminOrOrganizerOrUser();

        assertThrows(UnauthorizedActionException.class,
                () -> ticketService.viewTicketsByEvent(1L, pageable));

        verify(securityContextService).requireAdminOrOrganizerOrUser();
        verify(eventRepository, never()).findById(any());
    }
}
