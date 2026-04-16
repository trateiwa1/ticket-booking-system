package com.example.ticketbookingsystem.model;

import com.example.ticketbookingsystem.enums.BookingStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "booking")

public class Booking {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String referenceCode;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @OneToMany(mappedBy = "booking")
    private List<Ticket> tickets = new ArrayList<>();

    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public Booking(){}

    public Booking(BookingStatus status) {
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        this.referenceCode = "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.createdAt = LocalDateTime.now();
    }

    public Long getId(){
        return id;
    }

    public String getReference(){
        return referenceCode;
    }

    public User getOwner(){
        return owner;
    }

    public BookingStatus getStatus(){
        return status;
    }

    public List<Ticket> getTickets(){
        return Collections.unmodifiableList(tickets);
    }

    public void addTicket(Ticket ticket){
        this.tickets.add(ticket);
        ticket.setBooking(this);
    }

    public void removeTicket(Ticket ticket) {
        this.tickets.remove(ticket);
        ticket.setBooking(null);
    }

    public LocalDateTime getCreatedAt(){
        return createdAt;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setReferenceCode(String referenceCode){
        this.referenceCode = referenceCode;
    }

    public void setOwner(User owner){
        this.owner = owner;
    }

    public void setStatus(BookingStatus status){
        this.status = status;
    }

}
