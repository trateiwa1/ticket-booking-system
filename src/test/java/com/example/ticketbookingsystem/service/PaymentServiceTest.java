package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.PaymentRequest;
import com.example.ticketbookingsystem.dto.PaymentResponse;
import com.example.ticketbookingsystem.enums.*;
import com.example.ticketbookingsystem.exception.PaymentFailedException;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.PaymentMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Payment;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.PaymentRepository;
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
public class PaymentServiceTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private BookingRepository bookingRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private PaymentMapper paymentMapper;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private PaymentService paymentService;

    private User user1;
    private User user2;
    private Booking booking;
    private Ticket ticket;
    private Payment payment;
    private PaymentResponse paymentResponse;

    @BeforeEach
    void setUp() {
        user1 = new User("Thomas", "thomas@test.com", "Password", UserRole.USER);
        user1.setId(1L);

        user2 = new User("Pavel", "pavel@test.com", "Password", UserRole.USER);
        user2.setId(2L);

        Event event = new Event();
        event.setId(1L);

        ticket = new Ticket(event, TicketType.STANDARD, 100L, TicketStatus.RESERVED);

        booking = new Booking(BookingStatus.PENDING);
        booking.setId(1L);
        booking.setOwner(user1);
        booking.addTicket(ticket);

        payment = new Payment(booking, 100L, PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING);
        payment.setId(1L);

        paymentResponse = new PaymentResponse(
                1L, 1L, "BK-3F7A2E91", 100L,
                PaymentMethod.CREDIT_CARD, PaymentStatus.PENDING, LocalDateTime.now()
        );
    }

    //---------------------- SUCCESS ----------------------

    @Test
    void processPayment_success() {
        PaymentRequest request = new PaymentRequest(1L, 100L, PaymentMethod.CREDIT_CARD);

        doNothing().when(securityContextService).requireAdminOrUser();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(paymentMapper.mapToPayment(request, booking)).thenReturn(payment);
        when(paymentMapper.mapToResponse(payment)).thenReturn(paymentResponse);

        PaymentResponse result = paymentService.processPayment(request);

        assertNotNull(result);

        assertEquals(PaymentStatus.PAID, payment.getPaymentStatus());
        assertEquals(TicketStatus.SOLD, ticket.getStatus());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(paymentMapper).mapToPayment(request, booking);

        verify(ticketRepository).saveAll(anyList());
        verify(bookingRepository).save(booking);

        verify(paymentRepository).save(payment);
        verify(paymentMapper).mapToResponse(payment);
    }

    @Test
    void viewAllPayments() {
        Page<Payment> paymentPage = new PageImpl<>(List.of(payment));
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(securityContextService).requireAdmin();
        when(paymentRepository.findAll(pageable)).thenReturn(paymentPage);
        when(paymentMapper.mapToResponse(payment)).thenReturn(paymentResponse);

        Page<PaymentResponse> result = paymentService.viewAllPayments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());

        verify(securityContextService).requireAdmin();
        verify(paymentRepository).findAll(pageable);
        verify(paymentMapper).mapToResponse(payment);
    }

    //---------------------- FAILURES ----------------------

    @Test
    void processPayment_bookingNotFound() {
        PaymentRequest request = new PaymentRequest(1L, 100L, PaymentMethod.CREDIT_CARD);

        doNothing().when(securityContextService).requireAdminOrUser();
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> paymentService.processPayment(request));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);


        verify(paymentRepository, never()).save(any());
        verify(paymentMapper, never()).mapToPayment(any(), any());
        verify(ticketRepository, never()).saveAll(anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void processPayment_wrongAmount() {
        PaymentRequest request = new PaymentRequest(1L, 50L, PaymentMethod.CREDIT_CARD);

        doNothing().when(securityContextService).requireAdminOrUser();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(paymentMapper.mapToPayment(request, booking)).thenReturn(payment);

        assertThrows(PaymentFailedException.class,
                () -> paymentService.processPayment(request));

        verify(paymentMapper).mapToPayment(request, booking);
        verify(paymentRepository).save(payment);

        assertEquals(PaymentStatus.FAILED, payment.getPaymentStatus());

        verify(ticketRepository, never()).saveAll(anyList());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void processPayment_notBookingOwner() {
        PaymentRequest request = new PaymentRequest(1L, 100L, PaymentMethod.CREDIT_CARD);

        doNothing().when(securityContextService).requireAdminOrUser();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user2);

        assertThrows(UnauthorizedActionException.class,
                () -> paymentService.processPayment(request));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(securityContextService).getCurrentUser();

        verify(paymentMapper, never()).mapToPayment(any(), any());
        verify(ticketRepository, never()).saveAll(anyList());
        verify(bookingRepository, never()).save(any());
        verify(paymentRepository, never()).save(any());
    }

    @Test
    void viewAllPayments_notAdmin() {
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("Only ADMIN can view all payments"))
                .when(securityContextService).requireAdmin();

        assertThrows(UnauthorizedActionException.class,
                () -> paymentService.viewAllPayments(pageable));

        verify(securityContextService).requireAdmin();

        verify(paymentRepository, never()).findAll(any(Pageable.class));
        verify(paymentMapper, never()).mapToResponse(any());
    }
}


