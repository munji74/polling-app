package com.pollingapp.pollservice.repo;

import com.pollingapp.pollservice.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {}
