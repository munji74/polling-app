package com.pollingapp.pollservice.domain;

import jakarta.persistence.*;

@Entity
@Table(
        name = "votes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"pollId", "username"})
)
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long pollId;

    @Column(nullable = false)
    private Long candidateId;

    @Column(nullable = false)
    private String username; // from JWT subject

    public Vote() {}
    public Vote(Long pollId, Long candidateId, String username) {
        this.pollId = pollId; this.candidateId = candidateId; this.username = username;
    }

    public Long getId() { return id; }
    public Long getPollId() { return pollId; }
    public Long getCandidateId() { return candidateId; }
    public String getUsername() { return username; }
    public void setId(Long id) { this.id = id; }
    public void setPollId(Long pollId) { this.pollId = pollId; }
    public void setCandidateId(Long candidateId) { this.candidateId = candidateId; }
    public void setUsername(String username) { this.username = username; }
}
