package com.pollingapp.pollservice.api;

import com.pollingapp.pollservice.api.dto.CreatePollRequest;
import com.pollingapp.pollservice.domain.Poll;
import com.pollingapp.pollservice.repo.PollRepository;
import com.pollingapp.pollservice.repo.VoteRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/polls")
public class PollController {
    private final PollRepository polls;
    private final VoteRepository votes;

    public PollController(PollRepository polls, VoteRepository votes) {
        this.polls = polls; this.votes = votes;
    }

    @PostMapping
    public Poll create(@Valid @RequestBody CreatePollRequest req) {
        Instant closes = (req.closesAtIso() == null || req.closesAtIso().isBlank())
                ? null : Instant.parse(req.closesAtIso());
        return polls.save(new Poll(req.title(), closes));
    }

    @GetMapping("/{id}/tally/{candidateId}")
    public Map<String, Long> tally(@PathVariable Long id, @PathVariable Long candidateId) {
        long count = votes.countByPollIdAndCandidateId(id, candidateId);
        return Map.of("pollId", id, "candidateId", candidateId, "votes", count);
    }
}
