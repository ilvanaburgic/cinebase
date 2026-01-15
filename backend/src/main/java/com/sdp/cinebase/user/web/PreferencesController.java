package com.sdp.cinebase.user.web;

import com.sdp.cinebase.tmdb.dto.MovieDto;
import com.sdp.cinebase.tmdb.dto.PagedResponse;
import com.sdp.cinebase.tmdb.service.TmdbClient;
import com.sdp.cinebase.user.dto.OnboardingOptionDto;
import com.sdp.cinebase.user.dto.SavePicksRequest;
import com.sdp.cinebase.user.model.FavoritePick;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.FavoritePickRepository;
import com.sdp.cinebase.user.repo.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "User Preferences", description = "User onboarding and preference management for personalized recommendations")
@SecurityRequirement(name = "bearer-jwt")
public class PreferencesController {

    private final TmdbClient tmdbClient;
    private final FavoritePickRepository favoritePickRepository;
    private final UserRepository userRepository;

    public PreferencesController(
            TmdbClient tmdbClient,
            FavoritePickRepository favoritePickRepository,
            UserRepository userRepository
    ) {
        this.tmdbClient = tmdbClient;
        this.favoritePickRepository = favoritePickRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get 20 popular movies and TV shows for onboarding screen.
     * Returns 10 popular movies + 10 popular TV shows.
     */
    @Operation(summary = "Get onboarding options", description = "Get 20 popular movies and TV shows for user onboarding selection (10 movies + 10 TV shows)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Options retrieved successfully")
    })
    @GetMapping("/onboarding-options")
    public ResponseEntity<List<OnboardingOptionDto>> getOnboardingOptions() {
        List<OnboardingOptionDto> options = new ArrayList<>();

        // Fetch 10 popular movies
        PagedResponse<MovieDto> movies = tmdbClient.popularMovies(1);
        List<OnboardingOptionDto> movieOptions = movies.results().stream()
                .limit(10)
                .map(movie -> new OnboardingOptionDto(
                        movie.id(),
                        "movie",
                        movie.title() != null ? movie.title() : movie.name(),
                        movie.poster_path(),
                        movie.overview(),
                        movie.vote_average(),
                        extractGenreNames(movie.genre_ids())
                ))
                .toList();

        // Fetch 10 popular TV shows
        PagedResponse<MovieDto> tvShows = tmdbClient.popularTvShows(1);
        List<OnboardingOptionDto> tvOptions = tvShows.results().stream()
                .limit(10)
                .map(tv -> new OnboardingOptionDto(
                        tv.id(),
                        "tv",
                        tv.name() != null ? tv.name() : tv.title(),
                        tv.poster_path(),
                        tv.overview(),
                        tv.vote_average(),
                        extractGenreNames(tv.genre_ids())
                ))
                .toList();

        options.addAll(movieOptions);
        options.addAll(tvOptions);

        return ResponseEntity.ok(options);
    }

    /**
     * Check if the current user has completed onboarding (has saved picks).
     */
    @Operation(summary = "Check onboarding status", description = "Check if the user has completed the onboarding process by saving their favorite picks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/has-completed-onboarding")
    public ResponseEntity<Boolean> hasCompletedOnboarding(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean hasCompleted = favoritePickRepository.existsByUser(user);
        return ResponseEntity.ok(hasCompleted);
    }

    /**
     * Save user's 4 favorite picks during onboarding.
     */
    @Operation(summary = "Save user picks", description = "Save user's 4 favorite movie/TV show picks during onboarding for personalized AI recommendations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Picks saved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping("/save-picks")
    public ResponseEntity<Void> savePicks(
            @RequestBody SavePicksRequest request,
            Authentication authentication
    ) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Delete existing picks if any (in case user wants to redo onboarding)
        List<FavoritePick> existingPicks = favoritePickRepository.findByUser(user);
        if (!existingPicks.isEmpty()) {
            favoritePickRepository.deleteAll(existingPicks);
        }

        // Save new picks
        List<FavoritePick> picks = request.picks().stream()
                .map(pickItem -> new FavoritePick(
                        user,
                        pickItem.tmdbId(),
                        pickItem.mediaType(),
                        pickItem.title(),
                        pickItem.genres()
                ))
                .toList();

        favoritePickRepository.saveAll(picks);

        return ResponseEntity.ok().build();
    }

    /**
     * Helper method to convert genre IDs to genre names.
     * This is a simplified version - in production you'd fetch from TMDB genre list.
     */
    private String[] extractGenreNames(int[] genreIds) {
        if (genreIds == null || genreIds.length == 0) {
            return new String[0];
        }

        // Genre ID to name mapping (subset of common genres)
        // In production, fetch from TMDB /genre/movie/list and /genre/tv/list
        java.util.Map<Integer, String> genreMap = java.util.Map.ofEntries(
                java.util.Map.entry(28, "Action"),
                java.util.Map.entry(12, "Adventure"),
                java.util.Map.entry(16, "Animation"),
                java.util.Map.entry(35, "Comedy"),
                java.util.Map.entry(80, "Crime"),
                java.util.Map.entry(99, "Documentary"),
                java.util.Map.entry(18, "Drama"),
                java.util.Map.entry(10751, "Family"),
                java.util.Map.entry(14, "Fantasy"),
                java.util.Map.entry(36, "History"),
                java.util.Map.entry(27, "Horror"),
                java.util.Map.entry(10402, "Music"),
                java.util.Map.entry(9648, "Mystery"),
                java.util.Map.entry(10749, "Romance"),
                java.util.Map.entry(878, "Science Fiction"),
                java.util.Map.entry(10770, "TV Movie"),
                java.util.Map.entry(53, "Thriller"),
                java.util.Map.entry(10752, "War"),
                java.util.Map.entry(37, "Western"),
                java.util.Map.entry(10759, "Action & Adventure"),
                java.util.Map.entry(10762, "Kids"),
                java.util.Map.entry(10763, "News"),
                java.util.Map.entry(10764, "Reality"),
                java.util.Map.entry(10765, "Sci-Fi & Fantasy"),
                java.util.Map.entry(10766, "Soap"),
                java.util.Map.entry(10767, "Talk"),
                java.util.Map.entry(10768, "War & Politics")
        );

        return java.util.Arrays.stream(genreIds)
                .mapToObj(id -> genreMap.getOrDefault(id, "Unknown"))
                .toArray(String[]::new);
    }
}
