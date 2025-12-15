package com.sdp.cinebase.watchlist.dto;

public record AddWatchlistRequest(
    Long tmdbId,
    String mediaType,
    String title,
    String posterPath
) {}
