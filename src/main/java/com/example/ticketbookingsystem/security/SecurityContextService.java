package com.example.ticketbookingsystem.security;

import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.ResourceNotFoundException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityContextService {

    private final UserRepository userRepository;

    public SecurityContextService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getCurrentUserEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserRole getCurrentUserRole() {
        User user = getCurrentUser();
        return user.getRole();
    }

    public boolean isUser() {
        User user = getCurrentUser();
        return user.getRole() == UserRole.USER;
    }

    public boolean isAdmin() {
        User user = getCurrentUser();
        return user.getRole() == UserRole.ADMIN;
    }

    public boolean isOrganizer(){
        User user = getCurrentUser();
        return user.getRole() == UserRole.ORGANIZER;
    }

    public void requireUser(){
        if(!isUser()){
            throw new UnauthorizedActionException("Access denied: this action requires USER role");
        }
    }

    public void requireAdmin(){
        if(!isAdmin()){
            throw new UnauthorizedActionException("Access denied: this action requires ADMIN role");
        }
    }

    public void requireOrganizer(){
        if(!isOrganizer()){
            throw new UnauthorizedActionException("Access denied: this action requires ORGANIZER role");
        }
    }

    public void requireAdminOrOrganizer(){
        if(!isAdmin() && !isOrganizer()){
            throw new UnauthorizedActionException("Access denied: this action requires ADMIN or ORGANIZER role");
        }
    }

    public void requireAdminOrUser(){
        if(!isAdmin() && !isUser()){
            throw new UnauthorizedActionException("Access denied: this action requires ADMIN or USER role");
        }
    }

    public void requireAdminOrOrganizerOrUser(){
        if(!isAdmin() && !isOrganizer() && !isUser()){
            throw new UnauthorizedActionException("Access denied: this action requires ADMIN or ORGANIZER or USER role");
        }
    }

}
