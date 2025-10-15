package com.pollingapp.authservice.auth.dto;

public record AuthResponse(
        String accessToken,
        long expiresInSeconds
) {}
