package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.TicketType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreateTicketRequest {

    @NotNull(message = "Event ID is required")
    private Long eventId;

    @NotNull(message = "Ticket type is required")
    private TicketType type;

    @Positive(message = "Ticket price must be greater than 0")
    private Long price;

    public CreateTicketRequest(){}

    public CreateTicketRequest(Long eventId, TicketType type, Long price){
        this.eventId = eventId;
        this.type = type;
        this.price = price;
    }

    public Long getEventId(){
        return eventId;
    }

    public TicketType getType(){
        return type;
    }

    public Long getPrice(){
        return price;
    }

}
