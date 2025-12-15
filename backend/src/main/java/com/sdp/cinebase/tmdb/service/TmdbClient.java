package com.sdp.cinebase.tmdb.service;

import com.sdp.cinebase.tmdb.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

/**
 * Service client for interacting with The Movie Database (TMDB) API.
 * <p>
 * Provides methods to fetch movies, TV shows, trending content, and search results.
 * All API calls are proxied through this service to keep the TMDB API key secure on the backend.
 */
@Service
public class TmdbClient {

    private static final Logger log = LoggerFactory.getLogger(TmdbClient.class);

    private final WebClient client;
    private final String apiKey;

    private static final ParameterizedTypeReference<PagedResponse<MovieDto>> MOVIE_PAGE_TYPE =
            new ParameterizedTypeReference<>() {};

    /**
     * Constructs a new TmdbClient with the given base URL and API key.
     *
     * @param baseUrl the TMDB API base URL (e.g., {@code https://api.themoviedb.org/3})
     * @param apiKey the TMDB API key for authentication
     */
    public TmdbClient(
            @Value("${tmdb.api.base-url}") String baseUrl,
            @Value("${tmdb.api.key}") String apiKey
    ) {
        this.apiKey = apiKey;
        this.client = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.ACCEPT, "application/json")
                .build();

        log.info("TmdbClient initialized with base URL: {}", baseUrl);
    }

    public PagedResponse<MovieDto> popularMovies(int page) {
        log.debug("Fetching popular movies, page: {}", page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/movie/popular")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching popular movies (page {}): {} - {}",
                    page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch popular movies: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> topRatedMovies(int page) {
        log.debug("Fetching top-rated movies, page: {}", page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/movie/top_rated")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching top-rated movies (page {}): {} - {}",
                    page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch top-rated movies: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> latestDiscover(int page) {
        log.debug("Fetching latest movies, page: {}", page);
        try {
            String today = java.time.LocalDate.now().toString();

            return client.get()
                    .uri(uri -> uri.path("/discover/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("sort_by", "release_date.desc")
                            .queryParam("release_date.lte", today)
                            .queryParam("vote_count.gte", "10")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching latest movies (page {}): {} - {}",
                    page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch latest movies: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> searchMovies(String query, int page) {
        log.debug("Searching movies with query: '{}', page: {}", query, page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/search/movie")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("include_adult", false)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while searching movies (query: '{}', page {}): {} - {}",
                    query, page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to search movies: " + e.getStatusCode(), e);
        }
    }

    // ================================================
    // TV SHOWS
    // ================================================

    public PagedResponse<MovieDto> popularTvShows(int page) {
        log.debug("Fetching popular TV shows, page: {}", page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/tv/popular")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching popular TV shows (page {}): {} - {}",
                    page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch popular TV shows: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> topRatedTvShows(int page) {
        log.debug("Fetching top-rated TV shows, page: {}", page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/tv/top_rated")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching top-rated TV shows (page {}): {} - {}",
                    page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch top-rated TV shows: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> latestTvShows(int page) {
        log.debug("Fetching latest TV shows, page: {}", page);
        try {
            String today = java.time.LocalDate.now().toString();

            return client.get()
                    .uri(uri -> uri.path("/discover/tv")
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("sort_by", "first_air_date.desc")
                            .queryParam("first_air_date.lte", today)
                            .queryParam("vote_count.gte", "10")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching latest TV shows (page {}): {} - {}",
                    page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch latest TV shows: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> searchTvShows(String query, int page) {
        log.debug("Searching TV shows with query: '{}', page: {}", query, page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/search/tv")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("include_adult", false)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while searching TV shows (query: '{}', page {}): {} - {}",
                    query, page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to search TV shows: " + e.getStatusCode(), e);
        }
    }

    /**
     * Multi-search: searches both movies and TV shows in a single request.
     * <p>
     * Uses TMDB's /search/multi endpoint which returns both movies and TV shows
     * mixed together in the results.
     *
     * @param query the search query string
     * @param page the page number to fetch
     * @return paginated response with both movies and TV shows
     */
    public PagedResponse<MovieDto> searchMulti(String query, int page) {
        log.debug("Multi-searching (movies + TV) with query: '{}', page: {}", query, page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/search/multi")
                            .queryParam("api_key", apiKey)
                            .queryParam("query", query)
                            .queryParam("include_adult", false)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while multi-searching (query: '{}', page {}): {} - {}",
                    query, page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to multi-search: " + e.getStatusCode(), e);
        }
    }

    // ================================================
    // TRENDING (MIXED MOVIES + TV)
    // ================================================

    /**
     * Fetches trending movies and TV shows for a given time window.
     *
     * @param page the page number to fetch
     * @param timeWindow the time window ("day" or "week")
     * @return paginated response with trending items
     */
    public PagedResponse<MovieDto> trendingAll(int page, String timeWindow) {
        log.debug("Fetching trending all (movies + TV), time window: {}, page: {}", timeWindow, page);
        try {
            return client.get()
                    .uri(uri -> uri.path("/trending/all/" + timeWindow)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("page", page)
                            .build())
                    .retrieve()
                    .bodyToMono(MOVIE_PAGE_TYPE)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching trending all (time: {}, page {}): {} - {}",
                    timeWindow, page, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch trending all: " + e.getStatusCode(), e);
        }
    }

    public PagedResponse<MovieDto> trendingDay(int page) {
        return trendingAll(page, "day");
    }


    // ================================================
    // COMBINED TOP RATED (MOVIES + TV)
    // ================================================

    /**
     * Fetches and combines top-rated movies and TV shows, sorted by rating.
     * <p>
     * This method merges results from both top-rated movies and TV shows endpoints,
     * sorts them by vote_average in descending order, and returns the top 20 items per page.
     *
     * @param page the page number (note: pagination is approximate due to merging)
     * @return paginated response with combined top-rated content, sorted by rating
     */
    public PagedResponse<MovieDto> combinedTopRated(int page) {
        log.debug("Fetching combined top-rated (movies + TV), page: {}", page);
        try {
            // Fetch both top-rated movies and TV shows
            PagedResponse<MovieDto> movies = topRatedMovies(page);
            PagedResponse<MovieDto> tvShows = topRatedTvShows(page);

            // Combine and sort by vote_average descending
            var combined = new java.util.ArrayList<>(movies.results());
            combined.addAll(tvShows.results());

            combined.sort((a, b) -> Double.compare(b.vote_average(), a.vote_average()));

            // Limit to 20 results per page (standard TMDB page size)
            int endIndex = Math.min(combined.size(), 20);
            var results = combined.subList(0, endIndex);

            // Create combined response
            return new PagedResponse<>(
                    page,
                    results,
                    movies.total_pages() + tvShows.total_pages(),
                    movies.total_results() + tvShows.total_results()
            );
        } catch (Exception e) {
            log.error("Error while fetching combined top-rated (page {}): {}", page, e.getMessage());
            throw new RuntimeException("Failed to fetch combined top-rated: " + e.getMessage(), e);
        }
    }

    /**
     * Fetches and combines latest movies and TV shows, sorted by release date descending.
     * <p>
     * This method merges results from both latest movies and TV shows endpoints,
     * sorts them by release date (release_date for movies, first_air_date for TV),
     * and returns the top 20 most recently released items per page.
     *
     * @param page the page number (note: pagination is approximate due to merging)
     * @return paginated response with combined latest content, sorted by date descending
     */
    public PagedResponse<MovieDto> combinedLatest(int page) {
        log.debug("Fetching combined latest (movies + TV), page: {}", page);
        try {
            // Fetch both latest movies and TV shows
            PagedResponse<MovieDto> movies = latestDiscover(page);
            PagedResponse<MovieDto> tvShows = latestTvShows(page);

            // Combine and sort by date descending (most recent first)
            var combined = new java.util.ArrayList<>(movies.results());
            combined.addAll(tvShows.results());

            // Sort by date: release_date for movies, first_air_date for TV
            combined.sort((a, b) -> {
                String dateA = a.release_date() != null ? a.release_date() : a.first_air_date();
                String dateB = b.release_date() != null ? b.release_date() : b.first_air_date();

                // Handle null dates - put them at the end
                if (dateA == null && dateB == null) return 0;
                if (dateA == null) return 1;
                if (dateB == null) return -1;

                // Descending order (newest first)
                return dateB.compareTo(dateA);
            });

            // Limit to 20 results per page (standard TMDB page size)
            int endIndex = Math.min(combined.size(), 20);
            var results = combined.subList(0, endIndex);

            // Create combined response
            return new PagedResponse<>(
                    page,
                    results,
                    movies.total_pages() + tvShows.total_pages(),
                    movies.total_results() + tvShows.total_results()
            );
        } catch (Exception e) {
            log.error("Error while fetching combined latest (page {}): {}", page, e.getMessage());
            throw new RuntimeException("Failed to fetch combined latest: " + e.getMessage(), e);
        }
    }

    // ================================================
    // MOVIE DETAILS
    // ================================================

    public MovieDetailsDto getMovieDetails(int movieId) {
        log.debug("Fetching movie details for id: {}", movieId);
        try {
            return client.get()
                    .uri(uri -> uri.path("/movie/" + movieId)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("append_to_response", "credits,videos,reviews")
                            .build())
                    .retrieve()
                    .bodyToMono(MovieDetailsDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching movie details (id {}): {} - {}",
                    movieId, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch movie details: " + e.getStatusCode(), e);
        }
    }

    // ================================================
    // TV DETAILS
    // ================================================

    public MovieDetailsDto getTvDetails(int tvId) {
        log.debug("Fetching TV show details for id: {}", tvId);
        try {
            return client.get()
                    .uri(uri -> uri.path("/tv/" + tvId)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("append_to_response", "credits,videos,reviews")
                            .build())
                    .retrieve()
                    .bodyToMono(MovieDetailsDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching TV details (id {}): {} - {}",
                    tvId, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch TV details: " + e.getStatusCode(), e);
        }
    }

    public SeasonDetailsDto getSeasonDetails(int tvId, int seasonNumber) {
        log.debug("Fetching season details for TV {} season {}", tvId, seasonNumber);
        try {
            return client.get()
                    .uri(uri -> uri.path("/tv/" + tvId + "/season/" + seasonNumber)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .build())
                    .retrieve()
                    .bodyToMono(SeasonDetailsDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching season details (TV {}, season {}): {} - {}",
                    tvId, seasonNumber, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch season details: " + e.getStatusCode(), e);
        }
    }

    public PersonDetailsDto getPersonDetails(int personId) {
        log.debug("Fetching person details for id: {}", personId);
        try {
            return client.get()
                    .uri(uri -> uri.path("/person/" + personId)
                            .queryParam("api_key", apiKey)
                            .queryParam("language", "en-US")
                            .queryParam("append_to_response", "movie_credits,tv_credits")
                            .build())
                    .retrieve()
                    .bodyToMono(PersonDetailsDto.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("TMDB API error while fetching person details (id {}): {} - {}",
                    personId, e.getStatusCode(), e.getMessage());
            throw new RuntimeException("Failed to fetch person details: " + e.getStatusCode(), e);
        }
    }
}