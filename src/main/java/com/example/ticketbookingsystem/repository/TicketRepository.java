package com.example.ticketbookingsystem.repository;

import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    Page<Ticket> findByEvent(Event event, Pageable pageable);

    Page<Ticket> findByEventAndStatus(Event event, TicketStatus status, Pageable pageable);

    List<Ticket> findByEvent(Event event);
}
