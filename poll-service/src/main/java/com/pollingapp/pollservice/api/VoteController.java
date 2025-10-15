package com.pollingapp.pollservice.api;

import com.pollingapp.pollservice.api.dto.VoteRequest;
import com.pollingapp.pollservice.domain.Vote;
import com.pollingapp.pollservice.repo.VoteRepository;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    private final VoteRepository votes;
    public VoteController(VoteRepository votes) { this.votes = votes; }

    @PostMapping
    public Vote cast(@Valid @RequestBody VoteRequest req, Authentication auth) {
        String username = auth.getName(); // from JwtAuthFilter
        return votes.save(new Vote(req.pollId(), req.candidateId(), username));
    }
}
