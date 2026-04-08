package com.example.ticketbookingsystem.dto;

public class AuthenticationResponse {

    private Long id;
    private String token;
    private String email;
    private String role;

    public AuthenticationResponse(Long id, String token, String email, String role){
        this.id = id;
        this.token = token;
        this.email = email;
        this.role = role;
    }

    public Long getId(){
        return id;
    }

    public String getToken(){
        return token;
    }

    public String getEmail(){
        return email;
    }

    public String getRole(){
        return role;
    }
}
