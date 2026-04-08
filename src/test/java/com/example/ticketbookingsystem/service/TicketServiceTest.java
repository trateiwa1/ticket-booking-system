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
import com.example.ticketbookingsystem.security.AuthenticationHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class TicketServiceTest {

    @Mock private TicketRepository ticketRepository;
    @Mock private EventRepository eventRepository;
    @Mock private TicketMapper ticketMapper;
    @Mock private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private TicketService ticketService;

    private User owner;
    private Event event;
    private Ticket ticket;
    private CreateTicketRequest request;

    @BeforeEach
    void setUp() {

        owner = new User("John", "john@test.com", "Password", UserRole.ORGANIZER);
        owner.setId(1L);

        event = new Event();
        event.setOwner(owner);

        request = new CreateTicketRequest(1L, TicketType.STANDARD, 100L);

        ticket = new Ticket(event, TicketType.STANDARD, 100L, TicketStatus.AVAILABLE);
    }

    @Test
    void generateTicket_success() {
        doNothing().when(authenticationHelper).requireAdminOrOrganizer();
        when(authenticationHelper.isAdmin()).thenReturn(true);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(ticketMapper.mapToTicket(request, event)).thenReturn(ticket);

        TicketResponse response = mock(TicketResponse.class);
        when(ticketMapper.mapToResponse(ticket)).thenReturn(response);

        TicketResponse result = ticketService.generateTicket(request);

        assertNotNull(result);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void generateTicket_eventNotFound() {
        doNothing().when(authenticationHelper).requireAdminOrOrganizer();
        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> ticketService.generateTicket(request));
    }

    @Test
    void generateTicket_notEventOwner() {
        User other = new User("Tom", "tom@test.com", "Password", UserRole.ORGANIZER);
        other.setId(2L);

        doNothing().when(authenticationHelper).requireAdminOrOrganizer();
        when(authenticationHelper.isAdmin()).thenReturn(false);
        when(authenticationHelper.getCurrentUser()).thenReturn(other);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(UnauthorizedActionException.class,
                () -> ticketService.generateTicket(request));
    }

}
