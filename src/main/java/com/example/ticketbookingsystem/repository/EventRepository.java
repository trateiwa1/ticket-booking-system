package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByOwner(User owner, Pageable pageable);
}
