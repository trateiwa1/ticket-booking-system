package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.EventCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public class CreateEventRequest {

    @NotBlank(message = "Event name is required")
    private String name;

    @NotBlank(message = "Event description is required")
    private String description;

    @NotNull(message = "Event category is required")
    private EventCategory category;

    @Positive(message = "Event capacity must be greater than 0")
    private int totalCapacity;

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @NotNull(message = "Event start date and time is required")
    private LocalDateTime startDateTime;

    @NotNull(message = "Event end date and time is required")
    private LocalDateTime endDateTime;

    public CreateEventRequest(String name, String description, EventCategory category, int totalCapacity, Long venueId, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.name = name;
        this.description = description;
        this.category = category;
        this.totalCapacity = totalCapacity;
        this.venueId = venueId;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public EventCategory getCategory(){
        return category;
    }

    public int getTotalCapacity(){
        return totalCapacity;
    }

    public Long getVenueId(){
        return venueId;
    }

    public LocalDateTime getStartDateTime(){
        return startDateTime;
    }

    public LocalDateTime getEndDateTime(){
        return endDateTime;
    }

}
