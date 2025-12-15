package com.sdp.cinebase.review.dto;

import java.time.Instant;

public record ReviewResponse(
    Long id,
    Long userId,
    String username,
    Long tmdbId,
    String mediaType,
    String title,
    Integer rating,
    String reviewText,
    Instant createdAt,
    Instant updatedAt
) {}
