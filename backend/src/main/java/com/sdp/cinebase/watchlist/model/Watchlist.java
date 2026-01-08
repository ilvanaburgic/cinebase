package com.sdp.cinebase.watchlist.model;

import com.sdp.cinebase.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.Check;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "watchlist", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "tmdb_id", "media_type"})
})
@Check(constraints = "media_type IN ('movie', 'tv')")
public class Watchlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "tmdb_id", nullable = false)
    private Long tmdbId;

    @Column(name = "media_type", nullable = false, length = 20)
    @Pattern(regexp = "movie|tv", message = "Media type must be 'movie' or 'tv'")
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
    public Watchlist() {
        // Default constructor for JPA
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

    // Helper method for backward compatibility and convenience
    public Long getUserId() {
        return user != null ? user.getId() : null;
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

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Watchlist watchlist = (Watchlist) o;

        if (id != null && watchlist.id != null) {
            return Objects.equals(id, watchlist.id);
        }

        // If IDs are null, compare by unique constraint fields
        return Objects.equals(getUserId(), watchlist.getUserId()) &&
               Objects.equals(tmdbId, watchlist.tmdbId) &&
               Objects.equals(mediaType, watchlist.mediaType);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        // Use unique constraint fields for hashCode
        return Objects.hash(getUserId(), tmdbId, mediaType);
    }

    @Override
    public String toString() {
        return "Watchlist{" +
                "id=" + id +
                ", userId=" + getUserId() +
                ", tmdbId=" + tmdbId +
                ", mediaType='" + mediaType + '\'' +
                ", title='" + title + '\'' +
                ", addedAt=" + addedAt +
                '}';
    }
}
