package com.sdp.cinebase.game.model;

import com.sdp.cinebase.user.model.User;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "game_scores")
public class GameScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Instant playedAt;

    @PrePersist
    protected void onCreate() {
        if (this.playedAt == null) {
            this.playedAt = Instant.now();
        }
    }

    // Constructors
    public GameScore() {
    }

    public GameScore(User user, Integer score) {
        this.user = user;
        this.score = score;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Instant getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(Instant playedAt) {
        this.playedAt = playedAt;
    }
}
