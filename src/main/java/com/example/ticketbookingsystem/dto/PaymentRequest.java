package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class PaymentRequest {

    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Positive(message = "Amount must be greater than 0")
    private Long amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;

    public PaymentRequest(){}

    public PaymentRequest(Long bookingId, Long amount, PaymentMethod paymentMethod){
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    public Long getBookingId(){
        return bookingId;
    }

    public Long getAmount(){
        return amount;
    }

    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }
}
