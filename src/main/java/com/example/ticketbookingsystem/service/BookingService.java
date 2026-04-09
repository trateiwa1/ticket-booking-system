package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.BookingResponse;
import com.example.ticketbookingsystem.dto.CreateBookingRequest;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.exception.IllegalTicketStateException;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.BookingMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final SecurityContextService securityContextService;
    private final BookingMapper bookingMapper;

    public BookingService(BookingRepository bookingRepository, TicketRepository ticketRepository, SecurityContextService securityContextService, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.ticketRepository = ticketRepository;
        this.securityContextService = securityContextService;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {

        securityContextService.requireAdminOrUser();
        Booking booking = bookingMapper.mapToBooking(request);
        booking.setOwner(securityContextService.getCurrentUser());

        List<Ticket> tickets = ticketRepository.findAllById(request.getTicketIds());

        if (tickets.isEmpty()) {
            throw new IllegalTicketStateException("No tickets found for the provided IDs");
        }

        if(tickets.size() != request.getTicketIds().size()) {
            throw new ResourceNotFoundException("One or more tickets not found");
        }

        Event event = tickets.get(0).getEvent();

        for (Ticket ticket : tickets) {
            if (!ticket.getEvent().getId().equals(event.getId())) {
                throw new IllegalTicketStateException("All tickets must belong to the same event");
            }
        }

        for(Ticket ticket : tickets){
            if(ticket.getStatus() != TicketStatus.AVAILABLE){
                throw new IllegalTicketStateException("Ticket with ID " + ticket.getId() + " is not available");
            }

            ticket.setStatus(TicketStatus.RESERVED);
            booking.addTicket(ticket);
        }

        ticketRepository.saveAll(tickets);
        bookingRepository.save(booking);

        return bookingMapper.mapToResponse(booking);
    }


    public Page<BookingResponse> viewMyBookings(Pageable pageable) {

        securityContextService.requireAdminOrUser();

        Page<Booking> bookingPage = bookingRepository.findByOwner(securityContextService.getCurrentUser(), pageable);

        return bookingPage.map(bookingMapper::mapToResponse);

    }

    public BookingResponse getBooking(Long bookingId) {

        securityContextService.requireAdminOrUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!securityContextService.isAdmin() && !booking.getOwner().getId().equals(securityContextService.getCurrentUser().getId())) {
            throw new UnauthorizedActionException("Action denied: you can only view your own bookings");
        }

        return bookingMapper.mapToResponse(booking);
    }

    @Transactional
    public void cancelBooking(Long bookingId){

        securityContextService.requireAdminOrUser();

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if(!securityContextService.isAdmin() && !booking.getOwner().getId().equals(securityContextService.getCurrentUser().getId())){
            throw new UnauthorizedActionException("Action denied: you can only cancel your own bookings");
        }

        List<Ticket> tickets = new ArrayList<>(booking.getTickets());

        for(Ticket ticket : tickets){

            if(ticket.getStatus() != TicketStatus.RESERVED){
                throw new IllegalTicketStateException( "Action denied: only RESERVED tickets can be cancelled."
                );
            }

            booking.removeTicket(ticket);
            ticket.setStatus(TicketStatus.AVAILABLE);
            ticketRepository.save(ticket);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }
}
