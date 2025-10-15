package com.pollingapp.pollservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates")
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Position position;

    public Candidate() {}
    public Candidate(String name, Position position) {
        this.name = name; this.position = position;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Position getPosition() { return position; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPosition(Position position) { this.position = position; }
}
