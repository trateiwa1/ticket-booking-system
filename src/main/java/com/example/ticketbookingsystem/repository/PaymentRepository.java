package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
