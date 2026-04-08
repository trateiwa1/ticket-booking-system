package com.example.ticketbookingsystem.security;

import com.example.ticketbookingsystem.dto.AuthenticationResponse;
import com.example.ticketbookingsystem.dto.LoginRequest;
import com.example.ticketbookingsystem.dto.RegisterRequest;
import com.example.ticketbookingsystem.enums.UserRole;
import com.example.ticketbookingsystem.exception.EmailAlreadyExistsException;
import com.example.ticketbookingsystem.exception.InvalidCredentialsException;
import com.example.ticketbookingsystem.exception.UnauthorizedActionException;
import com.example.ticketbookingsystem.model.User;
import com.example.ticketbookingsystem.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder encoder;
    private final CustomUserDetailsService userDetailsService;

    public AuthenticationService(UserRepository userRepository, JwtService jwtService, PasswordEncoder encoder, CustomUserDetailsService userDetailsService) {
            this.userRepository = userRepository;
            this.jwtService = jwtService;
            this.encoder = encoder;
            this.userDetailsService = userDetailsService;
        }

        public AuthenticationResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
                throw new EmailAlreadyExistsException("Error: User with email already exists.");
        }

        if (request.getRole() == UserRole.ADMIN) {
                throw new UnauthorizedActionException("Error: Cannot assign ADMIN role");
        }

        String encodedPassword = encoder.encode(request.getPassword());

        User user = new User(request.getName(), request.getEmail(), encodedPassword, request.getRole());
        userRepository.save(user);

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(user.getEmail()));

        return new AuthenticationResponse(user.getId(), token, user.getEmail(), user.getRole().name());

        }

        public AuthenticationResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if(!encoder.matches(request.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(userDetailsService.loadUserByUsername(request.getEmail()));

        return new AuthenticationResponse(user.getId(), token, user.getEmail(), user.getRole().name());

        }

}

