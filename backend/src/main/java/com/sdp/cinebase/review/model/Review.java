package com.sdp.cinebase.review.model;

import com.sdp.cinebase.user.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.hibernate.annotations.Check;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "reviews", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "tmdb_id", "media_type"})
})
@Check(constraints = "media_type IN ('movie', 'tv')")
@Check(constraints = "rating IS NULL OR (rating >= 1 AND rating <= 10)")
public class Review {

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

    @Column(name = "rating")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 10, message = "Rating must be at most 10")
    private Integer rating; // 1-10, nullable (može biti samo review bez ratinga)

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Constructors
    public Review() {
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

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        if (id != null && review.id != null) {
            return Objects.equals(id, review.id);
        }
        return Objects.equals(getUserId(), review.getUserId()) &&
               Objects.equals(tmdbId, review.tmdbId) &&
               Objects.equals(mediaType, review.mediaType);
    }

    @Override
    public int hashCode() {
        if (id != null) {
            return Objects.hash(id);
        }
        return Objects.hash(getUserId(), tmdbId, mediaType);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", userId=" + getUserId() +
                ", tmdbId=" + tmdbId +
                ", mediaType='" + mediaType + '\'' +
                ", title='" + title + '\'' +
                ", rating=" + rating +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
