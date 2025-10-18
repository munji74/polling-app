package com.example.pollservice.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PollController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        return Map.of("status", "ok", "time", Instant.now().toString());
    }

    // public: landing page
    @GetMapping("/polls")
    public List<Map<String, Object>> listPolls() {
        // temporary static response to prove wiring; we’ll back this with DB next
        return List.of(
                Map.of(
                        "id", 1, "question", "Favorite JS bundler?",
                        "expiresAt", Instant.now().plusSeconds(3600).toString(),
                        "status", "ACTIVE",
                        "options", List.of(
                                Map.of("id", 10, "text", "Vite", "votes", 0),
                                Map.of("id", 11, "text", "Webpack", "votes", 0)
                        ),
                        "totalVotes", 0
                )
        );
    }

    // protected: create
    @PostMapping("/polls")
    public ResponseEntity<?> createPoll() {
        return ResponseEntity.status(201).body(Map.of("message", "Created (stub)"));
    }
}
