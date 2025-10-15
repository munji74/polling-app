package com.pollingapp.pollservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePollRequest(
        @NotBlank String title,
        String closesAtIso // optional ISO-8601 (e.g., 2025-12-31T23:59:59Z)
) {}
