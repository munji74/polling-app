package com.pollingapp.pollservice.api.dto;

import jakarta.validation.constraints.NotNull;

public record VoteRequest(
        @NotNull Long pollId,
        @NotNull Long candidateId
) {}
