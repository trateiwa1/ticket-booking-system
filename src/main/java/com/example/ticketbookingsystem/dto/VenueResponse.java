package com.example.ticketbookingsystem.dto;

public class VenueResponse {

    private Long id;

    private String name;

    private String city;

    private String address;

    private int maxCapacity;

    public VenueResponse(Long id, String name, String city, String address, int maxCapacity){
        this.id = id;
        this.name = name;
        this.city = city;
        this.address = address;
        this.maxCapacity = maxCapacity;
    }

    public Long getId(){
        return id;
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
