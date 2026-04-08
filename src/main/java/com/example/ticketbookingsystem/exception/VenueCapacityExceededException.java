package com.example.ticketbookingsystem.exception;

public class VenueCapacityExceededException extends RuntimeException{
    public VenueCapacityExceededException(String message){
        super(message);
    }
}
