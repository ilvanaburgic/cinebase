package com.sdp.cinebase.favorite.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "favorites", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "tmdb_id", "media_type"})
})
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Column(name = "media_type", nullable = false, length = 20)
    private String mediaType; // "movie" or "tv"

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "added_at", nullable = false)
    private Instant addedAt;

    @PrePersist
    protected void onCreate() {
        if (this.addedAt == null) {
            this.addedAt = Instant.now();
        }
    }

    // Constructors
    public Favorite() {
        // Default constructor for JPA
    }

    public Favorite(Long userId, Long tmdbId, String mediaType, String title, String posterPath) {
        this.userId = userId;
        this.tmdbId = tmdbId;
        this.mediaType = mediaType;
        this.title = title;
        this.posterPath = posterPath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Long tmdbId) {
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

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Favorite favorite = (Favorite) o;

        if (id != null && favorite.id != null) {
            return Objects.equals(id, favorite.id);
        }

        // If IDs are null, compare by unique constraint fields
        return Objects.equals(userId, favorite.userId) &&
               Objects.equals(tmdbId, favorite.tmdbId) &&
               Objects.equals(mediaType, favorite.mediaType);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        // Use unique constraint fields for hashCode
        return Objects.hash(userId, tmdbId, mediaType);
    }

    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", userId=" + userId +
                ", tmdbId=" + tmdbId +
                ", mediaType='" + mediaType + '\'' +
                ", title='" + title + '\'' +
                ", addedAt=" + addedAt +
                '}';
    }
}
