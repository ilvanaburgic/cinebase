package com.sdp.cinebase.review.web;

import com.sdp.cinebase.review.dto.AddReviewRequest;
import com.sdp.cinebase.review.dto.ReviewResponse;
import com.sdp.cinebase.review.service.ReviewService;
import com.sdp.cinebase.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reviews")
@Tag(name = "Reviews", description = "Movie and TV show review management endpoints")
@SecurityRequirement(name = "bearer-jwt")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create a review", description = "Create a new review for a movie or TV show with rating and optional text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AddReviewRequest request
    ) {
        ReviewResponse response = reviewService.createReview(Long.parseLong(principal.getId()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Update a review", description = "Update an existing review for a specific media item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
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

    @Operation(summary = "Delete a review", description = "Delete a review for a specific media item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Review deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/{tmdbId}/{mediaType}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long tmdbId,
            @PathVariable String mediaType
    ) {
        reviewService.deleteReview(Long.parseLong(principal.getId()), tmdbId, mediaType);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all reviews for media", description = "Get all reviews for a specific movie or TV show")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully")
    })
    @GetMapping("/{tmdbId}/{mediaType}")
    public ResponseEntity<List<ReviewResponse>> getReviewsForMedia(
            @PathVariable Long tmdbId,
            @PathVariable String mediaType
    ) {
        List<ReviewResponse> reviews = reviewService.getReviewsForMedia(tmdbId, mediaType);
        return ResponseEntity.ok(reviews);
    }

    @Operation(summary = "Get user's review for media", description = "Get the authenticated user's review for a specific media item")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Review not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
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

    @Operation(summary = "Get all user's reviews", description = "Get all reviews created by the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getAllMyReviews(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<ReviewResponse> reviews = reviewService.getAllUserReviews(Long.parseLong(principal.getId()));
        return ResponseEntity.ok(reviews);
    }
}
