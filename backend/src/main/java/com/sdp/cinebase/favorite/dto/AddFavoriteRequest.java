package com.sdp.cinebase.favorite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddFavoriteRequest(
    @NotNull(message = "TMDB ID is required")
    Long tmdbId,

    @NotBlank(message = "Media type is required (movie or tv)")
    String mediaType,

    @NotBlank(message = "Title is required")
    String title,

    String posterPath
) {}
