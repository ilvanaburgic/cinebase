package com.sdp.cinebase.review.service;

import com.sdp.cinebase.email.EmailService;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import com.sdp.cinebase.review.dto.AddReviewRequest;
import com.sdp.cinebase.review.dto.ReviewResponse;
import com.sdp.cinebase.review.model.Review;
import com.sdp.cinebase.review.repo.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, EmailService emailService) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public ReviewResponse createReview(Long userId, AddReviewRequest request) {
        // Check if user already has a review for this item
        if (reviewRepository.existsByUser_IdAndTmdbIdAndMediaType(userId, request.tmdbId(), request.mediaType())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Review already exists. Use update endpoint.");
        }

        // Validate request
        validateReviewRequest(request);

        // Get user for email notification
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Review review = new Review();
        review.setUser(user);
        review.setTmdbId(request.tmdbId());
        review.setMediaType(request.mediaType());
        review.setTitle(request.title());
        review.setRating(request.rating());
        review.setReviewText(request.reviewText());

        Review saved = reviewRepository.save(review);

        // Send email confirmation asynchronously
        try {
            emailService.sendReviewConfirmation(
                    user.getEmail(),
                    user.getUsername(),
                    request.title(),
                    request.mediaType(),
                    request.rating() != null ? request.rating() / 2.0 : 5.0, // Convert 1-10 to 1-5 for stars
                    request.reviewText() != null ? request.reviewText() : "No review text provided"
            );
        } catch (Exception e) {
            // Log but don't fail the request if email fails
            // Email service logs errors internally
        }

        return toResponse(saved);
    }

    public ReviewResponse updateReview(Long userId, Long tmdbId, String mediaType, AddReviewRequest request) {
        Review review = reviewRepository.findByUser_IdAndTmdbIdAndMediaType(userId, tmdbId, mediaType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        // Validate request
        validateReviewRequest(request);

        review.setRating(request.rating());
        review.setReviewText(request.reviewText());
        review.setTitle(request.title());

        Review updated = reviewRepository.save(review);
        return toResponse(updated);
    }

    public void deleteReview(Long userId, Long tmdbId, String mediaType) {
        Review review = reviewRepository.findByUser_IdAndTmdbIdAndMediaType(userId, tmdbId, mediaType)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found"));

        reviewRepository.delete(review);
    }

    public List<ReviewResponse> getReviewsForMedia(Long tmdbId, String mediaType) {
        return reviewRepository.findByTmdbIdAndMediaTypeOrderByCreatedAtDesc(tmdbId, mediaType)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public Optional<ReviewResponse> getUserReview(Long userId, Long tmdbId, String mediaType) {
        return reviewRepository.findByUser_IdAndTmdbIdAndMediaType(userId, tmdbId, mediaType)
                .map(this::toResponse);
    }

    public List<ReviewResponse> getAllUserReviews(Long userId) {
        return reviewRepository.findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateReviewRequest(AddReviewRequest request) {
        // Validate: must have either rating or review text
        if (request.rating() == null && (request.reviewText() == null || request.reviewText().isBlank())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Must provide rating or review text");
        }

        // Validate rating range if provided
        if (request.rating() != null && (request.rating() < 1 || request.rating() > 10)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 10");
        }
    }

    private ReviewResponse toResponse(Review review) {
        String username = userRepository.findById(review.getUserId())
                .map(User::getUsername)
                .orElse("Unknown User");

        return new ReviewResponse(
                review.getId(),
                review.getUserId(),
                username,
                review.getTmdbId(),
                review.getMediaType(),
                review.getTitle(),
                review.getRating(),
                review.getReviewText(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}
