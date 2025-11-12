package com.sdp.cinebase.tmdb.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Universal DTO for both movies and TV shows from TMDB API.
 * <p>
 * TMDB uses different field names for movies vs TV shows:
 * - Movies: title, release_date
 * - TV Shows: name, first_air_date
 * <p>
 * This DTO accepts both formats and the frontend handles display logic.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record MovieDto(
        int id,

        // Movie-specific fields
        String title,
        String release_date,

        // TV show-specific fields
        String name,
        String first_air_date,

        // Common fields for both movies and TV shows
        String overview,
        String poster_path,
        String backdrop_path,
        double vote_average,
        int vote_count,
        String media_type,
        int[] genre_ids
) {}
