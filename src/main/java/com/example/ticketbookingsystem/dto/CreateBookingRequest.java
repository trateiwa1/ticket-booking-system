package com.example.ticketbookingsystem.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CreateBookingRequest {

    @NotEmpty(message = "At least one ticket ID must be provided")
    private List<Long> ticketIds;

    public CreateBookingRequest() {}

    public CreateBookingRequest(List<Long> ticketIds) {
        this.ticketIds = ticketIds;
    }

    public List<Long> getTicketIds() {
        return ticketIds;
    }

}