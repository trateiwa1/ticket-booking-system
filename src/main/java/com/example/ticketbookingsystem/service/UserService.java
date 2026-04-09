package com.example.ticketbookingsystem.service;

import com.example.ticketbookingsystem.dto.BookingResponse;
import com.example.ticketbookingsystem.dto.UpdateProfileRequest;
import com.example.ticketbookingsystem.dto.UserProfileResponse;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.mapper.BookingMapper;
import com.example.ticketbookingsystem.mapper.UserMapper;
import com.example.ticketbookingsystem.model.Booking;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.BookingRepository;
import com.example.ticketbookingsystem.repository.UserRepository;
import com.example.ticketbookingsystem.security.SecurityContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final SecurityContextService securityContextService;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, BookingRepository bookingRepository, SecurityContextService securityContextService, UserMapper userMapper, BookingMapper bookingMapper, PasswordEncoder encoder){
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.securityContextService = securityContextService;
        this.userMapper = userMapper;
        this.bookingMapper = bookingMapper;
        this.encoder = encoder;
    }

    public UserProfileResponse viewProfile(){
        securityContextService.requireAdminOrOrganizerOrUser();
        User user = securityContextService.getCurrentUser();
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }

    public Page<BookingResponse> viewBookingHistory(Pageable pageable){
        securityContextService.requireAdminOrUser();
        Page<Booking> bookingPage = bookingRepository.findByOwner(securityContextService.getCurrentUser(), pageable);
        return bookingPage.map(bookingMapper::mapToResponse);
    }

    public UserProfileResponse updateMyProfile(UpdateProfileRequest request){
        securityContextService.requireAdminOrOrganizerOrUser();
        userMapper.mapUpdateToUser(securityContextService.getCurrentUser(), request);

        if(request.getPassword() != null && !request.getPassword().isEmpty()){
            String password = request.getPassword();
            String encodedPassword = encoder.encode(password);
            securityContextService.getCurrentUser().setPassword(encodedPassword);
        }

        userRepository.save(securityContextService.getCurrentUser());

        return userMapper.mapUpdateToResponse(securityContextService.getCurrentUser());
    }

    public UserProfileResponse updateUserProfile(Long userId, UpdateProfileRequest request){

        securityContextService.requireAdmin();

        User user = userRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.mapUpdateToUser(user, request);

        if(request.getPassword() != null && !request.getPassword().isEmpty()){
            String password = request.getPassword();
            String encodedPassword = encoder.encode(password);
            user.setPassword(encodedPassword);
        }

        userRepository.save(user);

        return userMapper.mapUpdateToResponse(user);
    }


}
