package com.example.pollservice.api.dto;

import java.time.Instant;
import java.util.List;

public record PollResponse(
        Long id,
        String question,
        Instant expiresAt,
        String status,          // ACTIVE | EXPIRED
        long totalVotes,
        List<OptionDto> options
) {
    public record OptionDto(Long id, String text, long votes) {}
}
