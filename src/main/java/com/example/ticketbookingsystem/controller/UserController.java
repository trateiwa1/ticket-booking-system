package com.example.ticketbookingsystem.controller;

import com.example.ticketbookingsystem.dto.BookingResponse;
import com.example.ticketbookingsystem.dto.UpdateProfileRequest;
import com.example.ticketbookingsystem.dto.UserProfileResponse;
import com.example.ticketbookingsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> viewProfile(){
        UserProfileResponse response = userService.viewProfile();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/bookings")
    public ResponseEntity<Page<BookingResponse>> viewMyBookings(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingResponse> response = userService.viewBookingHistory(pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse response = userService.updateMyProfile(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}") //user
    public ResponseEntity<UserProfileResponse> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateProfileRequest request){
        UserProfileResponse response = userService.updateUserProfile(userId, request);
        return ResponseEntity.ok(response);
    }

}
