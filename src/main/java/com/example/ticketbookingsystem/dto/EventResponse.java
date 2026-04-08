package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.EventCategory;
import java.time.LocalDateTime;

public class EventResponse {

    private Long id;

    private String name;

    private String description;

    private Long ownerId;

    private String ownerEmail;

    private EventCategory category;

    private int totalCapacity;

    private Long venueId;

    private String venueName;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    public EventResponse(Long id, String name, String description, Long ownerId,String ownerEmail, EventCategory category, int totalCapacity, Long venueId, String venueName, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.id = id;
        this.name = name;
        this.description = description;
        this.ownerId = ownerId;
        this.ownerEmail = ownerEmail;
        this.category = category;
        this.totalCapacity = totalCapacity;
        this.venueId = venueId;
        this.venueName = venueName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Long getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public Long getOwnerId(){return ownerId;}

    public String getOwnerEmail(){
        return ownerEmail;
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

    public String getVenueName(){
        return venueName;
    }

    public LocalDateTime getStartDateTime(){
        return startDateTime;
    }

    public LocalDateTime getEndDateTime(){
        return endDateTime;
    }
}
