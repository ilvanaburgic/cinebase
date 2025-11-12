package com.sdp.cinebase.tmdb.web;

import com.sdp.cinebase.tmdb.dto.MovieDto;
import com.sdp.cinebase.tmdb.dto.PagedResponse;
import com.sdp.cinebase.tmdb.service.TmdbClient;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that proxies TMDB API requests through the backend.
 * <p>
 * This controller provides endpoints for fetching movies, TV shows, and trending content.
 * All requests are forwarded to the TmdbClient service, which handles the actual TMDB API calls.
 */
@RestController
@RequestMapping("/api/tmdb")
public class TmdbProxyController {

    private final TmdbClient tmdb;

    public TmdbProxyController(TmdbClient tmdb) {
        this.tmdb = tmdb;
    }

    // ================================================
    // MOVIES
    // ================================================
    @GetMapping("/movies/popular")
    public PagedResponse<MovieDto> popularMovies(@RequestParam(defaultValue = "1") int page) {
        return tmdb.popularMovies(page);
    }

    @GetMapping("/movies/top-rated")
    public PagedResponse<MovieDto> topRatedMovies(@RequestParam(defaultValue = "1") int page) {
        return tmdb.topRatedMovies(page);
    }

    @GetMapping("/movies/latest")
    public PagedResponse<MovieDto> latestMovies(@RequestParam(defaultValue = "1") int page) {
        return tmdb.latestDiscover(page);
    }

    @GetMapping("/movies/search")
    public PagedResponse<MovieDto> searchMovies(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page
    ) {
        return tmdb.searchMovies(q, page);
    }

    // ================================================
    // TV SHOWS
    // ================================================

    @GetMapping("/tv/popular")
    public PagedResponse<MovieDto> tvPopular(@RequestParam(defaultValue = "1") int page) {
        return tmdb.popularTvShows(page);
    }

    @GetMapping("/tv/top-rated")
    public PagedResponse<MovieDto> tvTopRated(@RequestParam(defaultValue = "1") int page) {
        return tmdb.topRatedTvShows(page);
    }

    @GetMapping("/tv/latest")
    public PagedResponse<MovieDto> tvLatest(@RequestParam(defaultValue = "1") int page) {
        return tmdb.latestTvShows(page);
    }

    @GetMapping("/tv/search")
    public PagedResponse<MovieDto> tvSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page
    ) {
        return tmdb.searchTvShows(q, page);
    }

    // ================================================
    // MULTI SEARCH
    // ================================================

    @GetMapping("/multi/search")
    public PagedResponse<MovieDto> multiSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page
    ) {
        return tmdb.searchMovies(q, page);
    }

    // ================================================
    // FEED (COMBINED MOVIES + TV SHOWS)
    // ================================================
    @GetMapping("/feed/popular")
    public PagedResponse<MovieDto> feedPopular(@RequestParam(defaultValue = "1") int page) {
        // Trending today - mix of popular movies and TV shows
        return tmdb.trendingDay(page);
    }

    @GetMapping("/feed/latest")
    public PagedResponse<MovieDto> feedLatest(@RequestParam(defaultValue = "1") int page) {
        // Trending this week - recent popular content
        return tmdb.trendingWeek(page);
    }

    @GetMapping("/feed/top-rated")
    public PagedResponse<MovieDto> feedTopRated(@RequestParam(defaultValue = "1") int page) {
        // Combined top-rated movies and TV shows, sorted by rating
        return tmdb.combinedTopRated(page);
    }
}