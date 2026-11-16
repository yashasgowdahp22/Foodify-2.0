package com.foodify.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private String token;
    private Long id;
    private String name;
    private String email;
    private String role;

    public LoginResponse(String token) {
        this.token = token;
    }

    public LoginResponse(String token, Long id, String name, String email, String role) {
        this.token = token;
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }
}