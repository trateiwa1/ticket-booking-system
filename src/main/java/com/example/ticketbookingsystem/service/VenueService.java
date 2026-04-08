package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateVenueRequest;
import com.example.ticketbookingsystem.dto.VenueResponse;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.VenueMapper;
import com.example.ticketbookingsystem.model.Venue;
import com.example.ticketbookingsystem.repository.VenueRepository;
import com.example.ticketbookingsystem.security.AuthenticationHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VenueService {

    private final VenueRepository venueRepository;
    private final VenueMapper venueMapper;
    private final AuthenticationHelper authenticationHelper;

    public VenueService(VenueRepository venueRepository, VenueMapper venueMapper, AuthenticationHelper authenticationHelper){
        this.venueRepository = venueRepository;
        this.venueMapper = venueMapper;
        this.authenticationHelper = authenticationHelper;
    }

    public VenueResponse createVenue(CreateVenueRequest request){
        authenticationHelper.requireAdminOrOrganizer();
        Venue venue = venueMapper.mapToVenue(request);
        venueRepository.save(venue);
        return venueMapper.mapToResponse(venue);
    }

    public Page<VenueResponse> getVenues(Pageable pageable){
        authenticationHelper.requireAdminOrOrganizerOrUser();
        Page<Venue> venuePage = venueRepository.findAll(pageable);
        return venuePage.map(venueMapper::mapToResponse);
    }

    public VenueResponse getVenue(Long venueId){
        authenticationHelper.requireAdminOrOrganizerOrUser();

        Venue venue = venueRepository.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found"));

        return venueMapper.mapToResponse(venue);
    }
}
