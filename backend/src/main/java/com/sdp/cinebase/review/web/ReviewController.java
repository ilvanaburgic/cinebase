package com.sdp.cinebase.review.web;

import com.sdp.cinebase.review.dto.AddReviewRequest;
import com.sdp.cinebase.review.dto.ReviewResponse;
import com.sdp.cinebase.review.service.ReviewService;
import com.sdp.cinebase.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AddReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(Long.parseLong(principal.getId()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{tmdbId}/{mediaType}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long tmdbId,
            @PathVariable String mediaType,
            @RequestBody AddReviewRequest request
    ) {
        ReviewResponse response = reviewService.updateReview(Long.parseLong(principal.getId()), tmdbId, mediaType, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{tmdbId}/{mediaType}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long tmdbId,
            @PathVariable String mediaType
    ) {
        reviewService.deleteReview(Long.parseLong(principal.getId()), tmdbId, mediaType);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{tmdbId}/{mediaType}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForMedia(
            @PathVariable Long tmdbId,
            @PathVariable String mediaType
    ) {
        List<ReviewResponse> reviews = reviewService.getReviewsForMedia(tmdbId, mediaType);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/my/{tmdbId}/{mediaType}")
    public ResponseEntity<ReviewResponse> getUserReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long tmdbId,
            @PathVariable String mediaType
    ) {
        Optional<ReviewResponse> review = reviewService.getUserReview(Long.parseLong(principal.getId()), tmdbId, mediaType);
        return review.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getAllMyReviews(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ReviewResponse> reviews = reviewService.getAllUserReviews(Long.parseLong(principal.getId()));
        return ResponseEntity.ok(reviews);
    }
}
