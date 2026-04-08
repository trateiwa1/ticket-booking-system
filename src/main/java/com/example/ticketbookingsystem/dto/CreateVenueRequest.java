package com.example.ticketbookingsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class CreateVenueRequest {

    @NotBlank(message = "Venue name is required")
    private String name;

    @NotBlank(message = "Venue city is required")
    private String city;

    @NotBlank(message = "Venue address is required")
    private String address;

    @Positive(message = "Maximum capacity must be greater than 0")
    private int maxCapacity;

    public CreateVenueRequest(){}

    public CreateVenueRequest(String name, String city, String address, int maxCapacity){
        this.name = name;
        this.city = city;
        this.address = address;
        this.maxCapacity = maxCapacity;
    }

    public String getName(){
        return name;
    }

    public String getCity(){
        return city;
    }

    public String getAddress(){
        return address;
    }

    public int getMaxCapacity(){
        return maxCapacity;
    }

}
