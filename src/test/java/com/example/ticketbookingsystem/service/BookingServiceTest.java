package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.BookingResponse;
import com.example.ticketbookingsystem.dto.CreateBookingRequest;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.IllegalTicketStateException;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.BookingMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

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
public class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private TicketRepository ticketRepository;
    @Mock private SecurityContextService securityContextService;
    @Mock private BookingMapper bookingMapper;

    @InjectMocks
    private BookingService bookingService;

    private User user1;
    private User user2;
    private Event event;
    private Ticket ticket;
    private Booking booking;
    private BookingResponse bookingResponse;
    private List<Long> ticketIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        user1 = new User("Michael", "michael@test.com", "Password", UserRole.USER);
        user1.setId(1L);

        user2 = new User("Timothy", "timothy@test.com", "Password", UserRole.USER);
        user2.setId(2L);

        event = new Event();
        event.setId(1L);
        event.setOwner(user1);

        ticket = new Ticket(event, TicketType.VIP, 200L, TicketStatus.AVAILABLE);

        booking = new Booking(BookingStatus.PENDING);
        booking.setId(1L);
        booking.setOwner(user1);
        booking.addTicket(ticket);

        bookingResponse = new BookingResponse(1L, "BK-3F7A2E91", BookingStatus.PENDING, ticketIds);
    }

    //---------------------- SUCCESS ----------------------

    @Test
    void createBooking_success() {
        CreateBookingRequest request = new CreateBookingRequest(List.of(1L));

        doNothing().when(securityContextService).requireAdminOrUser();
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(bookingMapper.mapToBooking(request)).thenReturn(booking);
        when(ticketRepository.findAllById(List.of(1L))).thenReturn(List.of(ticket));

        BookingResponse response = mock(BookingResponse.class);
        when(bookingMapper.mapToResponse(booking)).thenReturn(response);

        BookingResponse result = bookingService.createBooking(request);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrUser();
        verify(securityContextService).getCurrentUser();
        verify(bookingMapper).mapToBooking(request);
        verify(bookingMapper).mapToResponse(booking);
        verify(ticketRepository).saveAll(List.of(ticket));
        verify(bookingRepository).save(booking);
    }

    @Test
    void viewMyBookings() {
        Page<Booking> bookingPage = new PageImpl<>(List.of(booking));
        Pageable pageable = PageRequest.of(0, 10);

        doNothing().when(securityContextService).requireAdminOrUser();
        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(bookingRepository.findByOwner(user1, pageable)).thenReturn(bookingPage);
        when(bookingMapper.mapToResponse(booking)).thenReturn(bookingResponse);

        Page<BookingResponse> result = bookingService.viewMyBookings(pageable);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrUser();
        verify(securityContextService).getCurrentUser();
        verify(bookingRepository).findByOwner(user1, pageable);
        verify(bookingMapper).mapToResponse(booking);
    }

    @Test
    void getBooking() {
        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user1);
        when(bookingMapper.mapToResponse(booking)).thenReturn(bookingResponse);

        BookingResponse result = bookingService.getBooking(1L);

        assertNotNull(result);
        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(securityContextService).getCurrentUser();
        verify(bookingMapper).mapToResponse(booking);
    }

    @Test
    void cancelBooking() {
        ticket.setStatus(TicketStatus.RESERVED);
        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        bookingService.cancelBooking(1L);

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(securityContextService).getCurrentUser();
        verify(ticketRepository).save(ticket);
        verify(bookingRepository).save(booking);
    }

    //---------------------- FAILURES ----------------------

    //CREATE BOOKING

    @Test
    void createBooking_differentEvents() {
        Event event2 = new Event();
        event2.setId(2L);

        Ticket ticket2 = new Ticket(event2, TicketType.VIP, 200L, TicketStatus.AVAILABLE);

        CreateBookingRequest request = new CreateBookingRequest(List.of(1L, 2L));

        doNothing().when(securityContextService).requireAdminOrUser();
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(bookingMapper.mapToBooking(request)).thenReturn(new Booking());
        when(ticketRepository.findAllById(any())).thenReturn(List.of(ticket, ticket2));

        assertThrows(IllegalTicketStateException.class,
                () -> bookingService.createBooking(request));

        verify(ticketRepository, never()).saveAll(any());
        verify(bookingRepository, never()).save(booking);
        verify(bookingMapper, never()).mapToResponse(any());
    }

    @Test
    void createBooking_ticketNotFound() {
        CreateBookingRequest request = new CreateBookingRequest(List.of(1L, 2L));

        doNothing().when(securityContextService).requireAdminOrUser();
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(bookingMapper.mapToBooking(request)).thenReturn(new Booking());
        when(ticketRepository.findAllById(any())).thenReturn(List.of(ticket));

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.createBooking(request));

        verify(securityContextService).requireAdminOrUser();
        verify(securityContextService).getCurrentUser();
        verify(bookingMapper).mapToBooking(request);
        verify(ticketRepository).findAllById(any());

        verify(ticketRepository, never()).saveAll(anyList());
        verify(bookingRepository, never()).save(booking);
        verify(bookingMapper, never()).mapToResponse(any());
    }

    @Test
    void createBooking_ticketNotAvailable() {
        ticket.setStatus(TicketStatus.SOLD);

        CreateBookingRequest request = new CreateBookingRequest(List.of(1L));

        doNothing().when(securityContextService).requireAdminOrUser();
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(bookingMapper.mapToBooking(request)).thenReturn(new Booking());
        when(ticketRepository.findAllById(any())).thenReturn(List.of(ticket));

        assertThrows(IllegalTicketStateException.class,
                () -> bookingService.createBooking(request));

        verify(securityContextService).requireAdminOrUser();
        verify(securityContextService).getCurrentUser();
        verify(bookingMapper).mapToBooking(request);
        verify(ticketRepository).findAllById(any());

        verify(ticketRepository, never()).saveAll(anyList());
        verify(bookingRepository, never()).save(booking);
        verify(bookingMapper, never()).mapToResponse(any());
    }

    @Test
    void createBooking_noTicketsFound() {
        CreateBookingRequest request = new CreateBookingRequest(List.of(1L));

        doNothing().when(securityContextService).requireAdminOrUser();
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        when(bookingMapper.mapToBooking(request)).thenReturn(new Booking());
        when(ticketRepository.findAllById(any())).thenReturn(List.of());

        assertThrows(IllegalTicketStateException.class,
                () -> bookingService.createBooking(request));

        verify(ticketRepository, never()).saveAll(anyList());
        verify(bookingRepository, never()).save(booking);
    }


    //VIEW MY BOOKINGS

    @Test
    void viewMyBookings_notAuthorized() {
        Pageable pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("This action requires ADMIN or USER role"))
                .when(securityContextService).requireAdminOrUser();

        assertThrows(UnauthorizedActionException.class,
                () -> bookingService.viewMyBookings(pageable));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository, never()).findByOwner(any(), any());
        verify(bookingMapper, never()).mapToResponse(booking);
    }


    //GET BOOKING

    @Test
    void getBooking_notFound() {
        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.getBooking(1L));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);

        verify(securityContextService, never()).getCurrentUser();
        verify(bookingMapper, never()).mapToResponse(booking);
    }

    @Test
    void getBooking_NotOwner() {
        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user2);

        assertThrows(UnauthorizedActionException.class,
                () -> bookingService.getBooking(1L));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(securityContextService).getCurrentUser();
        verify(bookingMapper, never()).mapToResponse(booking);
    }


    //CANCEL BOOKING

    @Test
    void cancelBooking_invalidTicketState() {
        ticket.setStatus(TicketStatus.SOLD);

        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user1);

        assertThrows(IllegalTicketStateException.class,
                () -> bookingService.cancelBooking(1L));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(securityContextService).getCurrentUser();

        verify(ticketRepository, never()).save(ticket);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void cancelBooking_NotFound() {
        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> bookingService.cancelBooking(1L));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);

        verify(securityContextService, never()).getCurrentUser();
        verify(ticketRepository, never()).save(ticket);
        verify(bookingRepository, never()).save(booking);
    }

    @Test
    void cancelBooking_notOwner() {
        doNothing().when(securityContextService).requireAdminOrUser();

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(securityContextService.getCurrentUser()).thenReturn(user2);

        assertThrows(UnauthorizedActionException.class,
                () -> bookingService.cancelBooking(1L));

        verify(securityContextService).requireAdminOrUser();
        verify(bookingRepository).findById(1L);
        verify(securityContextService).getCurrentUser();

        verify(ticketRepository, never()).save(ticket);
        verify(bookingRepository, never()).save(booking);
    }

}
