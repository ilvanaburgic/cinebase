package com.sdp.cinebase.review.dto;

public record AddReviewRequest(
    Long tmdbId,
    String mediaType,
    String title,
    Integer rating,
    String reviewText
) {}
