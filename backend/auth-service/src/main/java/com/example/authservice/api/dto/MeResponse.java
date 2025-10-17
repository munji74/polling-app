package com.example.authservice.api.dto;

import java.util.Set;

public record MeResponse(Long id, String name, String email, Set<String> roles) {}
