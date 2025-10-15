package com.pollingapp.pollservice.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "polls")
public class Poll {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private Instant closesAt; // nullable

    public Poll() {}
    public Poll(String title, Instant closesAt) {
        this.title = title; this.closesAt = closesAt;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Instant getClosesAt() { return closesAt; }
    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setClosesAt(Instant closesAt) { this.closesAt = closesAt; }
}
