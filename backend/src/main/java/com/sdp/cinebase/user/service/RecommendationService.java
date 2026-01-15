package com.sdp.cinebase.user.service;

import com.sdp.cinebase.tmdb.dto.MovieDto;
import com.sdp.cinebase.tmdb.dto.PagedResponse;
import com.sdp.cinebase.tmdb.service.TmdbClient;
import com.sdp.cinebase.user.model.FavoritePick;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.FavoritePickRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AI-Powered Recommendation Service using TMDB's machine learning algorithms.
 * This service leverages TMDB's /similar and /recommendations endpoints which use:
 * - Genre similarity
 * - Keyword matching
 * - Cast & crew overlap
 * - User viewing patterns (collaborative filtering)
 * - Content-based filtering algorithms
 * We aggregate recommendations from multiple sources and use frequency-based scoring
 * to ensure the most relevant content bubbles to the top.
 */
@Service
public class RecommendationService {

    private static final Logger log = LoggerFactory.getLogger(RecommendationService.class);

    private final FavoritePickRepository favoritePickRepository;
    private final TmdbClient tmdbClient;

    public RecommendationService(FavoritePickRepository favoritePickRepository, TmdbClient tmdbClient) {
        this.favoritePickRepository = favoritePickRepository;
        this.tmdbClient = tmdbClient;
    }

    /**
     * Generate AI-powered personalized recommendations for a user.
     * Algorithm:
     * 1. For each of user's 4 favorite picks, fetch:
     *    - TMDB's "similar" content (based on metadata: genres, keywords, etc.)
     *    - TMDB's "recommended" content (based on ML/collaborative filtering)
     * 2. Aggregate all recommendations into a frequency map
     * 3. Score each movie based on:
     *    - Frequency (how many times it appears in recommendations)
     *    - Genre overlap with user's favorites
     *    - Vote average (quality indicator)
     * 4. Return top-scored items, excluding user's already-picked favorites
     */
    public List<MovieDto> getRecommendations(User user, @SuppressWarnings("unused") int page) {
        log.debug("Generating AI recommendations for user: {}", user.getUsername());

        // Get user's favorite picks
        List<FavoritePick> picks = favoritePickRepository.findByUser(user);

        if (picks.isEmpty()) {
            log.warn("User {} has no favorite picks, returning trending content", user.getUsername());
            // Fallback to trending if no picks (page parameter unused for fallback)
            PagedResponse<MovieDto> trending = tmdbClient.trendingDay(1);
            return trending.results();
        }

        // Extract picked IDs to exclude from recommendations
        Set<Integer> pickedTmdbIds = picks.stream()
                .map(pick -> pick.getTmdbId().intValue())
                .collect(Collectors.toSet());

        // Extract genre preferences
        Map<String, Integer> genreWeights = extractGenreWeights(picks);

        log.debug("Genre weights for user {}: {}", user.getUsername(), genreWeights);

        // Aggregate recommendations from all user's favorites
        Map<Integer, RecommendationCandidate> candidateMap = new HashMap<>();

        for (FavoritePick pick : picks) {
            log.debug("Fetching recommendations based on: {} ({})", pick.getTitle(), pick.getMediaType());

            try {
                // Fetch similar and recommended content from TMDB
                List<MovieDto> similar = fetchSimilarContent(pick);
                List<MovieDto> recommended = fetchRecommendedContent(pick);

                // Process similar content (higher weight)
                for (MovieDto movie : similar) {
                    if (!pickedTmdbIds.contains(movie.id())) {
                        addOrUpdateCandidate(candidateMap, movie, 2.0); // Similar = 2.0 weight
                    }
                }

                // Process recommended content (medium weight)
                for (MovieDto movie : recommended) {
                    if (!pickedTmdbIds.contains(movie.id())) {
                        addOrUpdateCandidate(candidateMap, movie, 1.5); // Recommended = 1.5 weight
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching recommendations for pick {}: {}", pick.getTitle(), e.getMessage());
                // Continue with other picks even if one fails
            }
        }

        log.debug("Total recommendation candidates: {}", candidateMap.size());

        // Score all candidates
        List<ScoredMovie> scoredMovies = candidateMap.values().stream()
                .map(candidate -> {
                    double score = calculateAdvancedScore(candidate, genreWeights);
                    return new ScoredMovie(candidate.movie, score);
                })
                .sorted(Comparator.comparingDouble(ScoredMovie::score).reversed())
                .toList();

        // Return top recommendations (limit to 40 items)
        return scoredMovies.stream()
                .limit(40)
                .map(ScoredMovie::movie)
                .toList();
    }

    /**
     * Fetch similar content from TMDB for a given pick.
     */
    private List<MovieDto> fetchSimilarContent(FavoritePick pick) {
        try {
            PagedResponse<MovieDto> response;
            if ("movie".equals(pick.getMediaType())) {
                response = tmdbClient.getSimilarMovies(pick.getTmdbId().intValue(), 1);
            } else {
                response = tmdbClient.getSimilarTvShows(pick.getTmdbId().intValue(), 1);
            }
            return response != null ? response.results() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Failed to fetch similar content for {}: {}", pick.getTitle(), e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Fetch TMDB's AI recommendations for a given pick.
     */
    private List<MovieDto> fetchRecommendedContent(FavoritePick pick) {
        try {
            PagedResponse<MovieDto> response;
            if ("movie".equals(pick.getMediaType())) {
                response = tmdbClient.getMovieRecommendations(pick.getTmdbId().intValue(), 1);
            } else {
                response = tmdbClient.getTvRecommendations(pick.getTmdbId().intValue(), 1);
            }
            return response != null ? response.results() : Collections.emptyList();
        } catch (Exception e) {
            log.warn("Failed to fetch recommendations for {}: {}", pick.getTitle(), e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Add a movie to candidates or update its frequency if already present.
     */
    private void addOrUpdateCandidate(Map<Integer, RecommendationCandidate> candidateMap,
                                      MovieDto movie,
                                      double weight) {
        candidateMap.compute(movie.id(), (movieId, existing) -> {
            if (existing == null) {
                return new RecommendationCandidate(movie, weight);
            } else {
                existing.frequency += weight;
                return existing;
            }
        });
    }

    /**
     * Calculate advanced recommendation score based on multiple factors.
     * Scoring factors:
     * 1. Frequency (10 points per occurrence) - appears in multiple recommendation lists
     * 2. Genre overlap (5 points per matching genre × weight)
     * 3. Quality bonus (vote_average × 0.5, up to 5 points)
     * 4. Popularity bonus (vote_count / 1000, up to 2 points)
     */
    private double calculateAdvancedScore(RecommendationCandidate candidate,
                                         Map<String, Integer> genreWeights) {
        double score = 0.0;

        // 1. Frequency score - PRIMARY FACTOR (appears in multiple recommendations)
        score += candidate.frequency * 10.0;

        // 2. Genre overlap score
        int[] movieGenreIds = candidate.movie.genre_ids();
        if (movieGenreIds != null && movieGenreIds.length > 0) {
            String[] movieGenres = mapGenreIdsToNames(movieGenreIds);
            for (String genre : movieGenres) {
                Integer weight = genreWeights.get(genre);
                if (weight != null) {
                    score += weight * 5.0; // Genre match bonus
                }
            }
        }

        // 3. Quality bonus (high-rated content)
        score += candidate.movie.vote_average() * 0.5;

        // 4. Popularity bonus (widely watched content)
        score += Math.min(candidate.movie.vote_count() / 1000.0, 2.0);

        return score;
    }

    /**
     * Extract genres from user picks and calculate their weights (frequency).
     */
    private Map<String, Integer> extractGenreWeights(List<FavoritePick> picks) {
        Map<String, Integer> weights = new HashMap<>();

        for (FavoritePick pick : picks) {
            String genresJson = pick.getGenres();
            if (genresJson == null || genresJson.isEmpty()) {
                continue;
            }

            // Parse genres (stored as JSON array string like [\"Action\",\"Drama\"]")
            String[] genres = parseGenreArray(genresJson);
            for (String genre : genres) {
                weights.put(genre, weights.getOrDefault(genre, 0) + 1);
            }
        }

        return weights;
    }

    /**
     * Parse genre array from JSON string format.
     */
    private String[] parseGenreArray(String genresJson) {
        // Simple JSON array parsing (e.g., "[\"Action\",\"Drama\"]")
        if (genresJson.startsWith("[") && genresJson.endsWith("]")) {
            String content = genresJson.substring(1, genresJson.length() - 1);
            if (content.isEmpty()) {
                return new String[0];
            }
            return Arrays.stream(content.split(","))
                    .map(s -> s.trim().replaceAll("^\"|\"$", "")) // Remove quotes
                    .toArray(String[]::new);
        }
        return new String[0];
    }

    /**
     * Map genre IDs to genre names.
     */
    private String[] mapGenreIdsToNames(int[] genreIds) {
        Map<Integer, String> genreMap = Map.ofEntries(
                Map.entry(28, "Action"),
                Map.entry(12, "Adventure"),
                Map.entry(16, "Animation"),
                Map.entry(35, "Comedy"),
                Map.entry(80, "Crime"),
                Map.entry(99, "Documentary"),
                Map.entry(18, "Drama"),
                Map.entry(10751, "Family"),
                Map.entry(14, "Fantasy"),
                Map.entry(36, "History"),
                Map.entry(27, "Horror"),
                Map.entry(10402, "Music"),
                Map.entry(9648, "Mystery"),
                Map.entry(10749, "Romance"),
                Map.entry(878, "Science Fiction"),
                Map.entry(10770, "TV Movie"),
                Map.entry(53, "Thriller"),
                Map.entry(10752, "War"),
                Map.entry(37, "Western"),
                Map.entry(10759, "Action & Adventure"),
                Map.entry(10762, "Kids"),
                Map.entry(10763, "News"),
                Map.entry(10764, "Reality"),
                Map.entry(10765, "Sci-Fi & Fantasy"),
                Map.entry(10766, "Soap"),
                Map.entry(10767, "Talk"),
                Map.entry(10768, "War & Politics")
        );

        return Arrays.stream(genreIds)
                .mapToObj(id -> genreMap.getOrDefault(id, "Unknown"))
                .filter(name -> !"Unknown".equals(name))
                .toArray(String[]::new);
    }

    /**
     * Helper class to track recommendation candidates with frequency.
     */
    private static class RecommendationCandidate {
        MovieDto movie;
        double frequency; // How many times this appears in recommendations

        RecommendationCandidate(MovieDto movie, double initialWeight) {
            this.movie = movie;
            this.frequency = initialWeight;
        }
    }

    /**
     * Helper record to store movie with its recommendation score.
     */
    private record ScoredMovie(MovieDto movie, double score) {}
}
