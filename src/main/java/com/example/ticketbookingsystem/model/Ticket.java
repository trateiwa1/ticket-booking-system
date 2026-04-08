package com.example.ticketbookingsystem.model;

import com.example.ticketbookingsystem.enums.TicketStatus;
import com.example.ticketbookingsystem.enums.TicketType;
import jakarta.persistence.*;

@Entity
public class Ticket {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketType type;

    @Column(nullable = false)
    private Long price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    private Booking booking;

    public Ticket(){}

    public Ticket(Event event, TicketType type, Long price, TicketStatus status){
        this.event = event;
        this.type = type;
        this.price = price;
        this.status = status;
    }

    public Long getId(){
        return id;
    }

    public Event getEvent(){
        return event;
    }

    public TicketType getType(){
        return type;
    }

    public Long getPrice(){
        return price;
    }

    public TicketStatus getStatus(){
        return status;
    }

    public Booking getBooking(){
        return this.booking;
    }

    public void setStatus(TicketStatus status){
        this.status = status;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }


}
