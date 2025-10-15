package com.pollingapp.pollservice.repo;

import com.pollingapp.pollservice.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    long countByPollIdAndCandidateId(Long pollId, Long candidateId);
}
