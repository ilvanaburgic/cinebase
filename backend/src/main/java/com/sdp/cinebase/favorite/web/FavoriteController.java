package com.sdp.cinebase.favorite.web;

import com.sdp.cinebase.favorite.dto.AddFavoriteRequest;
import com.sdp.cinebase.favorite.dto.FavoriteResponse;
import com.sdp.cinebase.favorite.service.FavoriteService;
import com.sdp.cinebase.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@Tag(name = "Favorites", description = "User favorites management endpoints for movies and TV shows")
@SecurityRequirement(name = "bearer-jwt")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "Add to favorites", description = "Add a movie or TV show to user's favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Added to favorites successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddFavoriteRequest request) {
        FavoriteResponse response = favoriteService.addFavorite(Long.parseLong(principal.getId()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get user favorites", description = "Get all favorites for the authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Favorites retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public List<FavoriteResponse> getUserFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return favoriteService.getUserFavorites(Long.parseLong(principal.getId()));
    }

    @Operation(summary = "Remove from favorites", description = "Remove a movie or TV show from user's favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Removed from favorites successfully"),
            @ApiResponse(responseCode = "404", description = "Favorite not found"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        favoriteService.deleteFavorite(Long.parseLong(principal.getId()), id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if favorite", description = "Check if a specific media item is in user's favorites")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check completed successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long tmdbId,
            @RequestParam String mediaType) {
        boolean isFavorite = favoriteService.isFavorite(Long.parseLong(principal.getId()), tmdbId, mediaType);
        return ResponseEntity.ok(isFavorite);
    }
}
