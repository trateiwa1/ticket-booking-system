package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateVenueRequest;
import com.example.ticketbookingsystem.dto.VenueResponse;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.VenueMapper;
import com.example.ticketbookingsystem.model.Venue;
import com.example.ticketbookingsystem.repository.VenueRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
public class VenueServiceTest {

    @Mock private VenueRepository venueRepository;
    @Mock private VenueMapper venueMapper;
    @Mock private SecurityContextService securityContextService;

    @InjectMocks
    private VenueService venueService;

    private Venue venue;
    private VenueResponse venueResponse;  // ← ADD THIS

    @BeforeEach
    void setUp() {
        venue = new Venue("AT&T Arena", "San Antonio, TX", "Address", 5000);
        venue.setId(1L);

        venueResponse = new VenueResponse(1L, "AT&T Arena", "San Antonio, TX", "Address", 5000);
    }

    //---------------------- SUCCESS ----------------------

    @Test
    void createVenue() {
        CreateVenueRequest request = new CreateVenueRequest("AT&T Arena", "San Antonio, TX", "Address", 5000);

        doNothing().when(securityContextService).requireAdminOrOrganizer();

        when(venueMapper.mapToVenue(request)).thenReturn(venue);
        when(venueMapper.mapToResponse(venue)).thenReturn(venueResponse);

        VenueResponse result = venueService.createVenue(request);

        assertNotNull(result);
        assertEquals(venueResponse.getId(), result.getId());
        assertEquals(venueResponse.getName(), result.getName());

        verify(venueMapper).mapToVenue(request);
        verify(venueMapper).mapToResponse(venue);
        verify(venueRepository).save(venue);
    }

    @Test
    void getVenues() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        Page<Venue> venuePage = new PageImpl<>(List.of(venue));
        PageRequest pageable = PageRequest.of(0, 10);

        when(venueRepository.findAll(pageable)).thenReturn(venuePage);
        when(venueMapper.mapToResponse(venue)).thenReturn(venueResponse);

        Page<VenueResponse> result = venueService.getVenues(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(venueResponse.getId(), result.getContent().get(0).getId());
        assertEquals(venueResponse.getName(), result.getContent().get(0).getName());

        verify(venueRepository).findAll(pageable);
        verify(venueMapper).mapToResponse(venue);
    }

    @Test
    void getVenue() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();
        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(venueMapper.mapToResponse(venue)).thenReturn(venueResponse);

        VenueResponse result = venueService.getVenue(1L);

        assertNotNull(result);
        assertEquals(venueResponse.getId(), result.getId());
        assertEquals(venueResponse.getName(), result.getName());
        assertEquals(venueResponse.getAddress(), result.getAddress());

        verify(venueRepository).findById(1L);
        verify(venueMapper).mapToResponse(venue);
    }

    //---------------------- FAILURE ----------------------

    //CREATE VENUE

    @Test
    void createVenue_notAuthorized() {
        CreateVenueRequest request = new CreateVenueRequest("AT&T Arena", "San Antonio, TX", "Address", 5000);

        doThrow(new UnauthorizedActionException("This action requires ADMIN or ORGANIZER role"))
                .when(securityContextService).requireAdminOrOrganizer();

        assertThrows(UnauthorizedActionException.class,
                () -> venueService.createVenue(request));

        verify(venueRepository, never()).save(any());
        verify(venueMapper, never()).mapToVenue(any());
        verify(venueMapper, never()).mapToResponse(any());
    }

    //GET VENUES

    @Test
    void getVenues_notAuthorized() {
        PageRequest pageable = PageRequest.of(0, 10);

        doThrow(new UnauthorizedActionException("This action requires an authorized user"))
                .when(securityContextService).requireAdminOrOrganizerOrUser();

        assertThrows(UnauthorizedActionException.class,
                () -> venueService.getVenues(pageable));

        verify(venueRepository, never()).findAll();
        verify(venueMapper, never()).mapToResponse(any());
    }

    //GET VENUE

    @Test
    void getVenue_notFound() {
        doNothing().when(securityContextService).requireAdminOrOrganizerOrUser();

        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> venueService.getVenue(1L));

        verify(venueMapper, never()).mapToResponse(any());
    }

}