package com.example.ticketbookingsystem.dto;

import com.example.ticketbookingsystem.enums.BookingStatus;
import java.util.List;

public class BookingResponse {

    private Long bookingId;

    private String bookingReference;

    private BookingStatus status;

    private List<Long> ticketIds;

    public BookingResponse(Long bookingId, String bookingReference, BookingStatus status, List<Long> ticketIds){
        this.bookingId = bookingId;
        this.bookingReference = bookingReference;
        this.status = status;
        this.ticketIds = ticketIds;
    }

    public Long getId(){
        return bookingId;
    }

    public String getReference(){
        return bookingReference;
    }

    public BookingStatus getStatus(){
        return status;
    }

    public List<Long> getTicketIds(){
        return ticketIds;
    }
}
