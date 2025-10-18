package com.example.pollservice.poll;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByPollIdAndVoter(Long pollId, String voter);
    long countByOptionId(Long optionId);
    long countByPollId(Long pollId);
}
