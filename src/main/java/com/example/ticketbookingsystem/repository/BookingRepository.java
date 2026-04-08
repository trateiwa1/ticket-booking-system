package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByOwner(User owner, Pageable pageable);
}
