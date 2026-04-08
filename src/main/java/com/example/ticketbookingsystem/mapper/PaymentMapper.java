package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.PaymentRequest;
import com.example.ticketbookingsystem.dto.PaymentResponse;
import com.example.ticketbookingsystem.enums.PaymentStatus;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment mapToPayment(PaymentRequest request, Booking booking){
        return new Payment(booking, request.getAmount(), request.getPaymentMethod(), PaymentStatus.PENDING);
    }

    public PaymentResponse mapToResponse(Payment payment){
        return new PaymentResponse(payment.getId(),
                payment.getBooking().getId(),
                payment.getBooking().getReference(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                payment.getCreatedAt());
    }
}
