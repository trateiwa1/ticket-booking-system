package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.CreateTicketRequest;
import com.example.ticketbookingsystem.dto.TicketResponse;
import com.example.ticketbookingsystem.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService){
        this.ticketService = ticketService;
    }

    @GetMapping("/events/{eventId}/tickets")
    public ResponseEntity<Page<TicketResponse>> viewEventTickets(@PathVariable Long eventId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<TicketResponse> tickets = ticketService.viewTicketsByEvent(eventId,pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/events/{eventId}/tickets/available")
    public ResponseEntity<Page<TicketResponse>>  viewAvailableTickets(@PathVariable Long eventId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<TicketResponse> tickets = ticketService.viewAvailableTickets(eventId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @PostMapping("/tickets")
    public ResponseEntity<TicketResponse> generateTicket(@Valid @RequestBody CreateTicketRequest request){
        TicketResponse ticket = ticketService.generateTicket(request);
        return ResponseEntity
                .status(201)
                .body(ticket);
    }

}
