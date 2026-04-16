package com.example.ticketbookingsystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "venue")
public class Venue {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private int maxCapacity;

    public Venue(){}

    public Venue(String name, String city, String address, int maxCapacity){
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

    public void setId(Long id){
        this.id = id;
    }
}
