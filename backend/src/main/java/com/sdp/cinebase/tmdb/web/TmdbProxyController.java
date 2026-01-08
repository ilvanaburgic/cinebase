package com.sdp.cinebase.tmdb.web;

import com.sdp.cinebase.tmdb.dto.*;
import com.sdp.cinebase.tmdb.service.TmdbClient;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import com.sdp.cinebase.user.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller that proxies TMDB API requests through the backend.
 * <p>
 * This controller provides endpoints for fetching movies, TV shows, and trending content.
 * All requests are forwarded to the TmdbClient service, which handles the actual TMDB API calls.
 */
@RestController
@RequestMapping("/api/tmdb")
@Tag(name = "TMDB Proxy", description = "Third-party API integration - TMDB (The Movie Database) proxy endpoints for movies and TV shows")
public class TmdbProxyController {

    private final TmdbClient tmdb;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    public TmdbProxyController(
            TmdbClient tmdb,
            RecommendationService recommendationService,
            UserRepository userRepository
    ) {
        this.tmdb = tmdb;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
    }

    // ================================================
    // MOVIES
    // ================================================
    @Operation(summary = "Get popular movies", description = "Fetch popular movies from TMDB")
    @GetMapping("/movies/popular")
    public PagedResponse<MovieDto> popularMovies(@RequestParam(defaultValue = "1") int page) {
        return tmdb.popularMovies(page);
    }

    @Operation(summary = "Get top-rated movies", description = "Fetch top-rated movies from TMDB")
    @GetMapping("/movies/top-rated")
    public PagedResponse<MovieDto> topRatedMovies(@RequestParam(defaultValue = "1") int page) {
        return tmdb.topRatedMovies(page);
    }

    @Operation(summary = "Get latest movies", description = "Fetch latest movies from TMDB")
    @GetMapping("/movies/latest")
    public PagedResponse<MovieDto> latestMovies(@RequestParam(defaultValue = "1") int page) {
        return tmdb.latestDiscover(page);
    }

    @Operation(summary = "Search movies", description = "Search for movies by title")
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

    @Operation(summary = "Get popular TV shows", description = "Fetch popular TV shows from TMDB")
    @GetMapping("/tv/popular")
    public PagedResponse<MovieDto> tvPopular(@RequestParam(defaultValue = "1") int page) {
        return tmdb.popularTvShows(page);
    }

    @Operation(summary = "Get top-rated TV shows", description = "Fetch top-rated TV shows from TMDB")
    @GetMapping("/tv/top-rated")
    public PagedResponse<MovieDto> tvTopRated(@RequestParam(defaultValue = "1") int page) {
        return tmdb.topRatedTvShows(page);
    }

    @Operation(summary = "Get latest TV shows", description = "Fetch latest TV shows from TMDB")
    @GetMapping("/tv/latest")
    public PagedResponse<MovieDto> tvLatest(@RequestParam(defaultValue = "1") int page) {
        return tmdb.latestTvShows(page);
    }

    @Operation(summary = "Search TV shows", description = "Search for TV shows by title")
    @GetMapping("/tv/search")
    public PagedResponse<MovieDto> tvSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page
    ) {
        return tmdb.searchTvShows(q, page);
    }

    // ================================================
    // MULTI SEARCH (MOVIES + TV SHOWS)
    // ================================================

    /**
     * Multi-search endpoint that searches both movies and TV shows.
     * <p>
     * Uses TMDB's /search/multi API which returns both movies and TV shows
     * in a single request, mixed together in the results.
     *
     * @param q the search query string
     * @param page the page number (default 1)
     * @return paginated response with both movies and TV shows
     */
    @Operation(summary = "Multi-search", description = "Search for both movies and TV shows in a single request")
    @GetMapping("/multi/search")
    public PagedResponse<MovieDto> multiSearch(
            @RequestParam String q,
            @RequestParam(defaultValue = "1") int page
    ) {
        return tmdb.searchMulti(q, page);
    }

    // ================================================
    // FEED (COMBINED MOVIES + TV SHOWS)
    // ================================================

    @Operation(summary = "Get popular feed", description = "Fetch trending movies and TV shows for today")
    @GetMapping("/feed/popular")
    public PagedResponse<MovieDto> feedPopular(@RequestParam(defaultValue = "1") int page) {
        // Trending today - mix of popular movies and TV shows
        return tmdb.trendingDay(page);
    }

    @Operation(summary = "Get latest feed", description = "Fetch latest movies and TV shows combined")
    @GetMapping("/feed/latest")
    public PagedResponse<MovieDto> feedLatest(@RequestParam(defaultValue = "1") int page) {
        // Combined latest movies and TV shows, sorted by release date
        return tmdb.combinedLatest(page);
    }

    @Operation(summary = "Get top-rated feed", description = "Fetch top-rated movies and TV shows combined")
    @GetMapping("/feed/top-rated")
    public PagedResponse<MovieDto> feedTopRated(@RequestParam(defaultValue = "1") int page) {
        // Combined top-rated movies and TV shows, sorted by rating
        return tmdb.combinedTopRated(page);
    }

    @Operation(summary = "Get AI recommendations", description = "Get personalized AI-powered recommendations based on user's favorite picks using TMDB ML")
    @GetMapping("/feed/recommendations")
    public PagedResponse<MovieDto> feedRecommendations(
            @RequestParam(defaultValue = "1") int page,
            Authentication authentication
    ) {
        // AI-powered recommendations based on user's favorite picks
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<MovieDto> recommendations = recommendationService.getRecommendations(user, page);

        // Return as paged response (simplified - single page with all recommendations)
        return new PagedResponse<>(
                page,
                recommendations,
                1, // total_pages
                recommendations.size() // total_results
        );
    }

    // ================================================
    // MOVIE DETAILS
    // ================================================

    @Operation(summary = "Get movie details", description = "Fetch detailed information for a specific movie")
    @GetMapping("/movies/{id}")
    public MovieDetailsDto getMovieDetails(@PathVariable int id) {
        return tmdb.getMovieDetails(id);
    }

    // ================================================
    // TV DETAILS
    // ================================================

    @Operation(summary = "Get TV show details", description = "Fetch detailed information for a specific TV show")
    @GetMapping("/tv/{id}")
    public MovieDetailsDto getTvDetails(@PathVariable int id) {
        return tmdb.getTvDetails(id);
    }

    @Operation(summary = "Get season details", description = "Fetch detailed information for a specific season of a TV show")
    @GetMapping("/tv/{id}/season/{seasonNumber}")
    public SeasonDetailsDto getSeasonDetails(
            @PathVariable int id,
            @PathVariable int seasonNumber
    ) {
        return tmdb.getSeasonDetails(id, seasonNumber);
    }

    @Operation(summary = "Get person details", description = "Fetch detailed information about actors, directors, and other crew members")
    @GetMapping("/person/{id}")
    public PersonDetailsDto getPersonDetails(@PathVariable int id) {
        return tmdb.getPersonDetails(id);
    }
}