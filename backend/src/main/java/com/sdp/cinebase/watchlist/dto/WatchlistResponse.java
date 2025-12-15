package com.sdp.cinebase.watchlist.dto;

import java.time.Instant;

public record WatchlistResponse(
    Long id,
    Long tmdbId,
    String mediaType,
    String title,
    String posterPath,
    Instant addedAt
) {}
