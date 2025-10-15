package com.pollingapp.pollservice.api;

import com.pollingapp.pollservice.api.dto.CreatePositionRequest;
import com.pollingapp.pollservice.domain.Position;
import com.pollingapp.pollservice.repo.PositionRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {
    private final PositionRepository positions;
    public PositionController(PositionRepository positions) { this.positions = positions; }

    @PostMapping
    public Position create(@Valid @RequestBody CreatePositionRequest req) {
        return positions.save(new Position(req.name()));
    }

    @GetMapping
    public List<Position> all() { return positions.findAll(); }
}
