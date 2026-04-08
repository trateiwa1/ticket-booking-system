package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.CreateTicketRequest;
import com.example.ticketbookingsystem.dto.TicketResponse;
import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public Ticket mapToTicket(CreateTicketRequest request, Event event){
        return new Ticket(event, request.getType(), request.getPrice(), TicketStatus.AVAILABLE);
    }

    public TicketResponse mapToResponse(Ticket ticket){
        String venueAddress = ticket.getEvent().getVenue().getName() + ", " +
                ticket.getEvent().getVenue().getAddress() + ", " +
                ticket.getEvent().getVenue().getCity();

        return new TicketResponse(ticket.getId(),
                ticket.getEvent().getId(),
                ticket.getEvent().getName(),
                ticket.getType(),
                ticket.getPrice(),
                ticket.getStatus(),
                ticket.getEvent().getVenue().getName(),
                venueAddress,
                ticket.getEvent().getStartDateTime(),
                ticket.getEvent().getEndDateTime());
    }
}