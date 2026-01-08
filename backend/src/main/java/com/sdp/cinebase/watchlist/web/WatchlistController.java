package com.sdp.cinebase.watchlist.web;

import com.sdp.cinebase.security.UserPrincipal;
import com.sdp.cinebase.watchlist.dto.AddWatchlistRequest;
import com.sdp.cinebase.watchlist.dto.WatchlistResponse;
import com.sdp.cinebase.watchlist.service.WatchlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
@Tag(name = "Watchlist", description = "User watchlist management endpoints for movies and TV shows")
@SecurityRequirement(name = "bearer-jwt")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @Operation(summary = "Add to watchlist", description = "Add a movie or TV show to user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added to watchlist successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<WatchlistResponse> addToWatchlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AddWatchlistRequest request
    ) {
        WatchlistResponse response = watchlistService.addToWatchlist(Long.parseLong(principal.getId()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user watchlist", description = "Get all items in the authenticated user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getUserWatchlist(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<WatchlistResponse> watchlist = watchlistService.getUserWatchlist(Long.parseLong(principal.getId()));
        return ResponseEntity.ok(watchlist);
    }

    @Operation(summary = "Remove from watchlist", description = "Remove a movie or TV show from user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Removed from watchlist successfully"),
            @ApiResponse(responseCode = "404", description = "Watchlist item not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFromWatchlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        watchlistService.deleteFromWatchlist(Long.parseLong(principal.getId()), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if in watchlist", description = "Check if a specific media item is in user's watchlist")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkInWatchlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long tmdbId,
            @RequestParam String mediaType
    ) {
        boolean isInWatchlist = watchlistService.isInWatchlist(Long.parseLong(principal.getId()), tmdbId, mediaType);
        return ResponseEntity.ok(Map.of("isInWatchlist", isInWatchlist));
    }
}
