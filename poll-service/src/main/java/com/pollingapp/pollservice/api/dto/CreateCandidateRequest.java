package com.pollingapp.pollservice.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCandidateRequest(
        @NotBlank String name,
        @NotNull Long positionId
) {}
