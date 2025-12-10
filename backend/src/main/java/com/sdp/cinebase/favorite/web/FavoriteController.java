package com.sdp.cinebase.favorite.web;

import com.sdp.cinebase.favorite.dto.AddFavoriteRequest;
import com.sdp.cinebase.favorite.dto.FavoriteResponse;
import com.sdp.cinebase.favorite.service.FavoriteService;
import com.sdp.cinebase.security.UserPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping
    public ResponseEntity<FavoriteResponse> addFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddFavoriteRequest request) {
        FavoriteResponse response = favoriteService.addFavorite(Long.parseLong(principal.getId()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<FavoriteResponse> getUserFavorites(@AuthenticationPrincipal UserPrincipal principal) {
        return favoriteService.getUserFavorites(Long.parseLong(principal.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id) {
        favoriteService.deleteFavorite(Long.parseLong(principal.getId()), id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check")
    public ResponseEntity<Boolean> checkFavorite(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long tmdbId,
            @RequestParam String mediaType) {
        boolean isFavorite = favoriteService.isFavorite(Long.parseLong(principal.getId()), tmdbId, mediaType);
        return ResponseEntity.ok(isFavorite);
    }
}
