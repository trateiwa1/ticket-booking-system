package com.example.ticketbookingsystem.mapper;

import com.example.ticketbookingsystem.dto.UpdateProfileRequest;
import com.example.ticketbookingsystem.dto.UserProfileResponse;
import com.example.ticketbookingsystem.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public void mapUpdateToUser(User user, UpdateProfileRequest request){
        user.setName(request.getName());
    }

    public UserProfileResponse mapUpdateToResponse(User user){
        return new UserProfileResponse(user.getId(), user.getName(), user.getEmail(), user.getRole());
    }
}
