package com.example.pollservice.api;

import com.example.pollservice.api.dto.CreatePollRequest;
import com.example.pollservice.api.dto.PollResponse;
import com.example.pollservice.api.dto.VoteRequest;
import com.example.pollservice.poll.PollService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PollController {

    private final PollService polls;
    public PollController(PollService polls) { this.polls = polls; }

    // PUBLIC
    @GetMapping("/polls")
    public List<PollResponse> list() { return polls.listAll(); }

    // PUBLIC
    @GetMapping("/polls/{id}")
    public PollResponse get(@PathVariable Long id) { return polls.getOne(id); }

    // AUTH REQUIRED (SecurityConfig enforces it)
    @PostMapping("/polls")
    public ResponseEntity<PollResponse> create(@RequestBody @Valid CreatePollRequest req, Authentication auth) {
        var email = auth.getName(); // set by JwtAuthFilter
        var created = polls.create(req, email);
        return ResponseEntity.created(URI.create("/api/polls/" + created.id())).body(created);
    }

    // AUTH REQUIRED
    @PostMapping("/polls/{id}/votes")
    public ResponseEntity<PollResponse> vote(@PathVariable Long id,
                                             @RequestBody @Valid VoteRequest req,
                                             Authentication auth) {
        var email = auth.getName();
        var updated = polls.vote(id, req.optionId(), email);
        return ResponseEntity.ok(updated);
    }
}
