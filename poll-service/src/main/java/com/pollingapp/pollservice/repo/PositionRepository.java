package com.pollingapp.pollservice.repo;

import com.pollingapp.pollservice.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PositionRepository extends JpaRepository<Position, Long> {
    Optional<Position> findByName(String name);
}
