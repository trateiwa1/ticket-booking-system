package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.EventCategory;
import java.time.LocalDateTime;

public class UpdateEventRequest {

    private String name;
    private String description;
    private EventCategory category;
    private Integer totalCapacity;
    private Long venueId;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    public UpdateEventRequest(String name, String description, EventCategory category, Integer totalCapacity, Long venueId, LocalDateTime startDateTime, LocalDateTime endDateTime){
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

    public Integer getTotalCapacity(){
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
