package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.CreateEventRequest;
import com.example.ticketbookingsystem.dto.EventResponse;
import com.example.ticketbookingsystem.dto.UpdateEventRequest;
import com.example.ticketbookingsystem.service.EventService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService){
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<Page<EventResponse>> getEvents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EventResponse> events = eventService.getEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/me")
    public ResponseEntity<Page<EventResponse>> getMyEvents(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<EventResponse> events = eventService.getMyEvents(pageable);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long eventId){
        EventResponse event = eventService.getEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventResponse> createEvent(@Valid @RequestBody CreateEventRequest request){
        EventResponse event = eventService.createEvent(request);
        return ResponseEntity
                .status(201)
                .body(event);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long eventId, @RequestBody UpdateEventRequest request){
        EventResponse event = eventService.updateEvent(eventId, request);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId){
        eventService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

}
