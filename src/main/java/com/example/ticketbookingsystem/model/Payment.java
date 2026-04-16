package com.example.ticketbookingsystem.model;

import com.example.ticketbookingsystem.enums.PaymentMethod;
import com.example.ticketbookingsystem.enums.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")

public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(nullable = false)
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Payment(){}

    public Payment(Booking booking, Long amount, PaymentMethod paymentMethod, PaymentStatus paymentStatus){
        this.booking = booking;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public Booking getBooking(){
        return booking;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus){
        this.paymentStatus = paymentStatus;
    }

}
