package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.BookingResponse;
import com.example.ticketbookingsystem.dto.CreateBookingRequest;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.IllegalTicketStateException;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.BookingMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.security.AuthenticationHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private AuthenticationHelper authenticationHelper;
    @Mock private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Event event;
    private Ticket ticket;

    @BeforeEach
    void setUp() {

        user = new User("Michael", "michael@test.com", "Password", UserRole.USER);
        user.setId(1L);

        event = new Event();
        event.setId(1L);
        event.setOwner(user);

        ticket = new Ticket(event, TicketType.VIP, 200L, TicketStatus.AVAILABLE);
    }


    @Test
    void createBooking_success() {
        CreateBookingRequest request = new CreateBookingRequest(List.of(1L));

        doNothing().when(authenticationHelper).requireAdminOrUser();
        when(authenticationHelper.getCurrentUser()).thenReturn(user);

        Booking booking = new Booking(BookingStatus.PENDING);

        when(bookingMapper.mapToBooking(request)).thenReturn(booking);
        when(ticketRepository.findAllById(List.of(1L))).thenReturn(List.of(ticket));

        BookingResponse response = mock(BookingResponse.class);
        when(bookingMapper.mapToResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.createBooking(request);

        assertNotNull(result);
        verify(ticketRepository).saveAll(anyList());
        verify(bookingRepository).save(booking);
    }

    @Test
    void createBooking_ticketNotFound() {
        CreateBookingRequest request = new CreateBookingRequest(List.of(1L, 2L));

        doNothing().when(authenticationHelper).requireAdminOrUser();
        when(authenticationHelper.getCurrentUser()).thenReturn(user);

        when(bookingMapper.mapToBooking(request)).thenReturn(new Booking());
        when(ticketRepository.findAllById(any())).thenReturn(List.of(ticket));

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.createBooking(request));
    }

    @Test
    void createBooking_ticketNotAvailable() {
        ticket.setStatus(TicketStatus.SOLD);

        CreateBookingRequest request = new CreateBookingRequest(List.of(1L));

        doNothing().when(authenticationHelper).requireAdminOrUser();
        when(authenticationHelper.getCurrentUser()).thenReturn(user);

        when(bookingMapper.mapToBooking(request)).thenReturn(new Booking());
        when(ticketRepository.findAllById(any())).thenReturn(List.of(ticket));

        assertThrows(IllegalTicketStateException.class,
                () -> bookingService.createBooking(request));
    }

}
