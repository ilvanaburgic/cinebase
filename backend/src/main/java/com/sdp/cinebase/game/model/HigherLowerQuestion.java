package com.sdp.cinebase.game.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "higher_lower_questions")
public class HigherLowerQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String mediaType; // "movie" or "tv"

    @Column(nullable = false)
    private Integer tmdbId;

    @Column(nullable = false)
    private String title;

    private String posterPath;

    @Column(nullable = false)
    private String metric; // "IMDB Rating", "Number of Seasons", "Budget (millions)", etc.

    @Column(nullable = false)
    private Double value; // Actual numeric value

    @Column(nullable = false)
    private Instant createdAt;

    public HigherLowerQuestion() {}

    public HigherLowerQuestion(String mediaType, Integer tmdbId, String title, String posterPath, String metric, Double value) {
        this.mediaType = mediaType;
        this.tmdbId = tmdbId;
        this.title = title;
        this.posterPath = posterPath;
        this.metric = metric;
        this.value = value;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getMediaType() {
        return mediaType;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public String getTitle() {
        return title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getMetric() {
        return metric;
    }

    public Double getValue() {
        return value;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
