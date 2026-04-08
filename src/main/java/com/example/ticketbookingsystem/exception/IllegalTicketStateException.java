package com.example.ticketbookingsystem.exception;

public class IllegalTicketStateException extends RuntimeException{
    public IllegalTicketStateException(String message){
        super(message);
    }
}
