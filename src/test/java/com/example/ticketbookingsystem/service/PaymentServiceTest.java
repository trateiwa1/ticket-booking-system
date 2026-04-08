package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.PaymentRequest;
import com.example.ticketbookingsystem.dto.PaymentResponse;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.enums.PaymentMethod;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.PaymentFailedException;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.PaymentMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Payment;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.PaymentRepository;
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
public class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private PaymentMapper paymentMapper;
    @Mock private AuthenticationHelper authenticationHelper;

    @InjectMocks
    private PaymentService paymentService;

    private User user;
    private Booking booking;
    private Ticket ticket;

    @BeforeEach
    void setUp() {

        user = new User("Thomas", "thomas@test.com", "Password", UserRole.USER);
        user.setId(1L);

        Event event = new Event();
        event.setId(1L);

        ticket = new Ticket(event, TicketType.STANDARD, 100L, TicketStatus.RESERVED);

        booking = new Booking(BookingStatus.PENDING);
        booking.setOwner(user);
        booking.addTicket(ticket);

    }


    @Test
    void processPayment_successful() {
        PaymentRequest request = new PaymentRequest(1L,100L, PaymentMethod.CREDIT_CARD);

        doNothing().when(authenticationHelper).requireAdminOrUser();
        when(authenticationHelper.isAdmin()).thenReturn(false);
        when(authenticationHelper.getCurrentUser()).thenReturn(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Payment payment = new Payment();
        when(paymentMapper.mapToPayment(request, booking)).thenReturn(payment);

        PaymentResponse response = mock(PaymentResponse.class);
        when(paymentMapper.mapToResponse(payment)).thenReturn(response);

        PaymentResponse result = paymentService.processPayment(request);

        assertNotNull(result);
        assertEquals(TicketStatus.SOLD, ticket.getStatus());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        verify(ticketRepository).saveAll(anyList());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void processPayment_bookingNotFound() {
        PaymentRequest request = new PaymentRequest(1L, 100L, PaymentMethod.CREDIT_CARD);

        doNothing().when(authenticationHelper).requireAdminOrUser();
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.processPayment(request));
    }

    @Test
    void processPayment_wrongAmount() {
        PaymentRequest request = new PaymentRequest(1L, 50L, PaymentMethod.CREDIT_CARD);

        doNothing().when(authenticationHelper).requireAdminOrUser();
        when(authenticationHelper.isAdmin()).thenReturn(false);
        when(authenticationHelper.getCurrentUser()).thenReturn(user);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        Payment payment = new Payment();
        when(paymentMapper.mapToPayment(request, booking)).thenReturn(payment);

        assertThrows(PaymentFailedException.class,
                () -> paymentService.processPayment(request));

        verify(paymentRepository).save(payment);
    }

}
