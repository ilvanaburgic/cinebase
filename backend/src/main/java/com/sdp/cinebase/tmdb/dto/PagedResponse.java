package com.sdp.cinebase.tmdb.dto;

import java.util.List;

/**
 * Generic paginated response wrapper for TMDB API results.
 *
 * @param <T> the type of items in the results list (typically MovieDto)
 */
public record PagedResponse<T>(
        int page,
        List<T> results,
        int total_pages,
        int total_results
) {}
