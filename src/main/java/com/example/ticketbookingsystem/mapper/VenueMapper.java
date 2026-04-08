package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.CreateVenueRequest;
import com.example.ticketbookingsystem.dto.VenueResponse;
import com.example.ticketbookingsystem.model.Venue;
import org.springframework.stereotype.Component;

@Component
public class VenueMapper {

    public Venue mapToVenue(CreateVenueRequest request) {
        return new Venue(
                request.getName(),
                request.getCity(),
                request.getAddress(),
                request.getMaxCapacity()
        );
    }

    public VenueResponse mapToResponse(Venue venue) {
        return new VenueResponse(
                venue.getId(),
                venue.getName(),
                venue.getCity(),
                venue.getAddress(),
                venue.getMaxCapacity()
        );
    }
}