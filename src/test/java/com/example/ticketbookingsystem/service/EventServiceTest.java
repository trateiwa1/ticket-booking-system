package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.CreateEventRequest;
import com.example.ticketbookingsystem.dto.EventResponse;
import com.example.ticketbookingsystem.dto.UpdateEventRequest;
import com.example.ticketbookingsystem.enums.EventCategory;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.mapper.EventMapper;
import com.example.ticketbookingsystem.model.Event;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.model.Venue;
import com.example.ticketbookingsystem.repository.EventRepository;
import com.example.ticketbookingsystem.repository.VenueRepository;
import com.example.ticketbookingsystem.security.AuthenticationHelper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock private EventRepository eventRepository;
    @Mock private VenueRepository venueRepository;
    @Mock private AuthenticationHelper authenticationHelper;
    @Mock private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    private User owner;
    private Venue venue;
    private Event event;

    @BeforeEach
    void setUp() {

        owner = new User("Thomas", "tom@test.com", "Password", UserRole.ORGANIZER);
        owner.setId(1L);

        venue = new Venue("TD Garden Arena", "Boston, MA", "Address", 3000);
        venue.setId(1L);

        event = new Event();
        event.setOwner(owner);
    }

    @Test
    void createEvent_success() {
        CreateEventRequest request = new CreateEventRequest("NBA Playoffs", "Lakers vs Celtics",
                EventCategory.SPORTS, 2500, 1L, LocalDateTime.now(), LocalDateTime.now().plusHours(2)
        );

        doNothing().when(authenticationHelper).requireAdminOrOrganizer();
        when(authenticationHelper.getCurrentUser()).thenReturn(owner);

        when(venueRepository.findById(1L)).thenReturn(Optional.of(venue));
        when(eventMapper.mapToEvent(request, venue)).thenReturn(event);

        EventResponse response = mock(EventResponse.class);
        when(eventMapper.mapToResponse(event)).thenReturn(response);

        EventResponse result = eventService.createEvent(request);

        assertNotNull(result);
        verify(eventRepository).save(event);
    }

    @Test
    void createEvent_venueNotFound() {
        CreateEventRequest request = new CreateEventRequest(
                "Acting My Age", "Kevin Hart", EventCategory.COMEDY, 500,
                1L, LocalDateTime.now(), LocalDateTime.now()
        );

        doNothing().when(authenticationHelper).requireAdminOrOrganizer();
        when(venueRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> eventService.createEvent(request));
    }

}
