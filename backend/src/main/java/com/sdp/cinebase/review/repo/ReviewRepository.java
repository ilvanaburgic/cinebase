package com.sdp.cinebase.review.repo;

import com.sdp.cinebase.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByTmdbIdAndMediaTypeOrderByCreatedAtDesc(Long tmdbId, String mediaType);

    Optional<Review> findByUser_IdAndTmdbIdAndMediaType(Long userId, Long tmdbId, String mediaType);

    boolean existsByUser_IdAndTmdbIdAndMediaType(Long userId, Long tmdbId, String mediaType);

    List<Review> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
