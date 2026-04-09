package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.PaymentRequest;
import com.example.ticketbookingsystem.dto.PaymentResponse;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.enums.PaymentStatus;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.exception.PaymentFailedException;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.PaymentMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Payment;
import com.example.ticketbookingsystem.model.Ticket;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.PaymentRepository;
import com.example.ticketbookingsystem.repository.TicketRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final PaymentMapper paymentMapper;
    private final SecurityContextService securityContextService;

    public PaymentService(PaymentRepository paymentRepository, TicketRepository ticketRepository, BookingRepository bookingRepository, PaymentMapper paymentMapper, SecurityContextService securityContextService){
        this.paymentRepository = paymentRepository;
        this.ticketRepository = ticketRepository;
        this.bookingRepository = bookingRepository;
        this.paymentMapper = paymentMapper;
        this.securityContextService = securityContextService;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request){

        securityContextService.requireAdminOrUser();

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        if (!securityContextService.isAdmin() && !booking.getOwner().getId().equals(securityContextService.getCurrentUser().getId())) {
            throw new UnauthorizedActionException("You can only pay for your own booking");
        }

        Payment payment = paymentMapper.mapToPayment(request, booking);

        List<Ticket> tickets = booking.getTickets();

        Long total = 0L;

        for(Ticket ticket : tickets){
            Long price = ticket.getPrice();
            total += price;
        }

        if (!request.getAmount().equals(total)) {
            payment.setPaymentStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new PaymentFailedException(
                    String.format("Payment amount $%d does not match booking total $%d for booking ID %d",
                            request.getAmount(), total, booking.getId()));
        }



        for (Ticket ticket : tickets) {
            ticket.setStatus(TicketStatus.SOLD);
        }

        ticketRepository.saveAll(tickets);

        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        payment.setPaymentStatus(PaymentStatus.PAID);
        paymentRepository.save(payment);

        return paymentMapper.mapToResponse(payment);

    }

    public Page<PaymentResponse> viewAllPayments(Pageable pageable){

        securityContextService.requireAdmin();

        Page<Payment> paymentPage = paymentRepository.findAll(pageable);

        return paymentPage.map(paymentMapper::mapToResponse);
    }

}
