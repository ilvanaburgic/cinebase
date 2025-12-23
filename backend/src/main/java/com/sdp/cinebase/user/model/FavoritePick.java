package com.sdp.cinebase.user.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favorite_picks")
public class FavoritePick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer tmdbId;

    @Column(nullable = false)
    private String mediaType; // "movie" or "tv"

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String genres; // JSON array as string

    @Column(nullable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    // Constructors
    public FavoritePick() {
    }

    public FavoritePick(User user, Integer tmdbId, String mediaType, String title, String genres) {
        this.user = user;
        this.tmdbId = tmdbId;
        this.mediaType = mediaType;
        this.title = title;
        this.genres = genres;
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

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
