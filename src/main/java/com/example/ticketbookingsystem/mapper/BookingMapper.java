package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.BookingResponse;
import com.example.ticketbookingsystem.dto.CreateBookingRequest;
import com.example.ticketbookingsystem.enums.BookingStatus;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.Ticket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class BookingMapper {

    public Booking mapToBooking(CreateBookingRequest request){

        return new Booking(BookingStatus.PENDING);
    }

    public BookingResponse mapToResponse(Booking booking){

        List<Ticket> tickets = booking.getTickets();

        List<Long> ticketIds = new ArrayList<>();

        for(Ticket ticket : tickets){
            Long id = ticket.getId();
            ticketIds.add(id);
        }

        return new BookingResponse(booking.getId(),
                booking.getReference(),
                booking.getStatus(),
                ticketIds);
    }
}
