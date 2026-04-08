package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.CreateEventRequest;
import com.example.ticketbookingsystem.dto.EventResponse;
import com.example.ticketbookingsystem.dto.UpdateEventRequest;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Venue;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event mapToEvent(CreateEventRequest request, Venue venue){
        return new Event(request.getName(),
                request.getDescription(),
                request.getCategory(),
                request.getTotalCapacity(),
                venue,
                request.getStartDateTime(),
                request.getEndDateTime());
    }

    public void mapUpdateToEvent(Event event, UpdateEventRequest request, Venue venue){

        if (request.getName() != null) {
            event.setName(request.getName());
        }

        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }

        if (request.getCategory() != null) {
            event.setCategory(request.getCategory());
        }

        if (request.getTotalCapacity() != null) {
            event.setTotalCapacity(request.getTotalCapacity());
        }

        if (venue != null) {
            event.setVenue(venue);
        }

        if (request.getStartDateTime() != null) {
            event.setStartDateTime(request.getStartDateTime());
        }

        if (request.getEndDateTime() != null) {
            event.setEndDateTime(request.getEndDateTime());
        }
    }

    public EventResponse mapToResponse(Event event){
        return new EventResponse(event.getId(),
                event.getName(),
                event.getDescription(),
                event.getOwner().getId(),
                event.getOwner().getEmail(),
                event.getCategory(),
                event.getTotalCapacity(),
                event.getVenue().getId(),
                event.getVenue().getName(),
                event.getStartDateTime(),
                event.getEndDateTime());
    }

}


