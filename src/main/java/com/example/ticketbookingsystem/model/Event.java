package com.example.ticketbookingsystem.model;

import com.example.ticketbookingsystem.enums.EventCategory;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Event {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventCategory category;

    @Column(nullable = false)
    private int totalCapacity;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(nullable = false)
    private LocalDateTime startDateTime;

    @Column(nullable = false)
    private LocalDateTime endDateTime;

    public Event(){}

    public Event(String name, String description, EventCategory category, int totalCapacity, Venue venue, LocalDateTime startDateTime, LocalDateTime endDateTime){
        this.name = name;
        this.description = description;
        this.category = category;
        this.venue = venue;
        this.totalCapacity = totalCapacity;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
    }

    public Long getId(){
        return id;
    }

    public User getOwner(){
        return owner;
    }

    public String getName(){
        return name;
    }

    public String getDescription(){
        return description;
    }

    public EventCategory getCategory(){
        return category;
    }

    public Venue getVenue(){
        return venue;
    }

    public int getTotalCapacity(){
        return totalCapacity;
    }

    public LocalDateTime getStartDateTime(){
        return startDateTime;
    }

    public LocalDateTime getEndDateTime(){
        return endDateTime;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setCategory(EventCategory category){
        this.category = category;
    }

    public void setVenue(Venue venue){
        this.venue = venue;
    }

    public void setTotalCapacity(int totalCapacity){
        this.totalCapacity = totalCapacity;
    }

    public void setStartDateTime(LocalDateTime startDateTime){
        this.startDateTime = startDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime){
        this.endDateTime = endDateTime;
    }
}
