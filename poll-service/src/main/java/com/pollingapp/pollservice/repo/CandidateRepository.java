package com.pollingapp.pollservice.repo;

import com.pollingapp.pollservice.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    List<Candidate> findByPositionId(Long positionId);
}
