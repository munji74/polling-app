package com.example.authservice.api.dto;

public record AuthResponse(String accessToken, String tokenType) {
    public AuthResponse(String accessToken) { this(accessToken, "Bearer"); }
}
