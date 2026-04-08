package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.PaymentMethod;
import com.example.ticketbookingsystem.enums.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponse {

    private Long paymentId;

    private Long bookingId;

    private String bookingReference;

    private Long amount;

    private PaymentMethod paymentMethod;

    private PaymentStatus paymentStatus;

    private LocalDateTime createdAt;

    public PaymentResponse(Long paymentId, Long bookingId, String bookingReference, Long amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus, LocalDateTime createdAt){
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.createdAt = createdAt;
    }

    public Long getPaymentId(){
        return paymentId;
    }

    public Long getBookingId(){
        return bookingId;
    }

    public String getBookingReference(){
        return bookingReference;
    }

    public Long getAmount(){
        return amount;
    }

    public PaymentMethod getPaymentMethod(){
        return paymentMethod;
    }

    public PaymentStatus getPaymentStatus(){
        return paymentStatus;
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

}
