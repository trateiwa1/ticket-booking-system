package com.example.ticketbookingsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private final String error;
    private final String message;
    private final List<ValidationError> errors;
    private final LocalDateTime timestamp;

    public ErrorResponse(String error, String message){
        this.error = error;
        this.message = message;
        this.errors = null;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(String error, String message, List<ValidationError> errors){
        this.error = error;
        this.message = message;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }

    public String getError(){
        return error;
    }

    public String getMessage(){
        return message;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public List<ValidationError> getErrors(){
        return errors;
    }
}
