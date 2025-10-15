package com.pollingapp.pollservice.api;

import com.pollingapp.pollservice.api.dto.CreateCandidateRequest;
import com.pollingapp.pollservice.domain.Candidate;
import com.pollingapp.pollservice.domain.Position;
import com.pollingapp.pollservice.repo.CandidateRepository;
import com.pollingapp.pollservice.repo.PositionRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {
    private final CandidateRepository candidates;
    private final PositionRepository positions;

    public CandidateController(CandidateRepository candidates, PositionRepository positions) {
        this.candidates = candidates; this.positions = positions;
    }

    @PostMapping
    public Candidate create(@Valid @RequestBody CreateCandidateRequest req) {
        Position pos = positions.findById(req.positionId()).orElseThrow();
        return candidates.save(new Candidate(req.name(), pos));
    }

    @GetMapping("/by-position/{positionId}")
    public List<Candidate> byPosition(@PathVariable Long positionId) {
        return candidates.findByPositionId(positionId);
    }
}
