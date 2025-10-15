package com.pollingapp.pollservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePositionRequest(@NotBlank String name) {}
