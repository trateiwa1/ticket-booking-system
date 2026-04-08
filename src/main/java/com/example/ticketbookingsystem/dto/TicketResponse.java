package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;

import java.time.LocalDateTime;

public class TicketResponse {

    private Long id;

    private Long eventId;

    private String eventName;

    private TicketType type;

    private Long price;

    private TicketStatus status;

    private String venueName;

    private String venueAddress;

    private LocalDateTime eventStartDateTime;

    private LocalDateTime eventEndDateTime;


    public TicketResponse(Long id, Long eventId, String eventName, TicketType type, Long price, TicketStatus status,
                          String venueName, String venueAddress, LocalDateTime eventStartDateTime, LocalDateTime eventEndDateTime){
        this.id = id;
        this.eventId = eventId;
        this.eventName = eventName;
        this.type = type;
        this.price = price;
        this.status = status;
        this.venueName = venueName;
        this.venueAddress = venueAddress;
        this.eventStartDateTime = eventStartDateTime;
        this.eventEndDateTime = eventEndDateTime;
    }

    public Long getId(){
        return id;
    }

    public Long getEventId(){
        return eventId;
    }

    public String getEventName(){
        return eventName;
    }

    public TicketType getType(){
        return type;
    }

    public Long getPrice(){
        return price;
    }

    public TicketStatus getStatus(){
        return status;
    }

    public String getVenueName(){
        return venueName;
    }

    public String getVenueAddress(){
        return venueAddress;
    }


    public LocalDateTime getEventStartDateTime(){
        return eventStartDateTime;
    }

    public LocalDateTime getEventEndDateTime(){
        return eventEndDateTime;
    }

}
