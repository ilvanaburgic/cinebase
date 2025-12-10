package com.sdp.cinebase.favorite.dto;

import java.time.Instant;

public record FavoriteResponse(
    Long id,
    Long tmdbId,
    String mediaType,
    String title,
    String posterPath,
    Instant addedAt
) {}
