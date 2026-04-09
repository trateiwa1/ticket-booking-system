package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateVenueRequest;
import com.example.ticketbookingsystem.dto.VenueResponse;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.VenueMapper;
import com.example.ticketbookingsystem.model.Venue;
import com.example.ticketbookingsystem.repository.VenueRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class VenueServiceTest {

    @Mock
    private VenueRepository venueRepository;
    @Mock
    private VenueMapper venueMapper;
    @Mock
    private SecurityContextService securityContextService;

    @InjectMocks
    private VenueService venueService;

    private Venue venue;

    @BeforeEach
    void setUp() {
        venue = new Venue("AT&T Arena", "San Antonio, TX", "Address", 5000);
    }

    @Test
    void createVenue_Success() {
        CreateVenueRequest request = new CreateVenueRequest("AT&T Arena", "San Antonio, TX", "Address", 5000);

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(venueMapper.mapToVenue(request)).thenReturn(venue);

        VenueResponse response = mock(VenueResponse.class);
        when(venueMapper.mapToResponse(venue)).thenReturn(response);

        VenueResponse result = venueService.createVenue(request);

        assertNotNull(result);
        verify(venueRepository).save(venue);
    }

    @Test
    void getVenue_notFound() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> venueService.getVenue(1L));
    }

}