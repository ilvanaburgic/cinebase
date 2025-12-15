package com.sdp.cinebase.watchlist.web;

import com.sdp.cinebase.security.UserPrincipal;
import com.sdp.cinebase.watchlist.dto.AddWatchlistRequest;
import com.sdp.cinebase.watchlist.dto.WatchlistResponse;
import com.sdp.cinebase.watchlist.service.WatchlistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {

    private final WatchlistService watchlistService;

    public WatchlistController(WatchlistService watchlistService) {
        this.watchlistService = watchlistService;
    }

    @PostMapping
    public ResponseEntity<WatchlistResponse> addToWatchlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody AddWatchlistRequest request
    ) {
        WatchlistResponse response = watchlistService.addToWatchlist(Long.parseLong(principal.getId()), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<WatchlistResponse>> getUserWatchlist(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        List<WatchlistResponse> watchlist = watchlistService.getUserWatchlist(Long.parseLong(principal.getId()));
        return ResponseEntity.ok(watchlist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFromWatchlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long id
    ) {
        watchlistService.deleteFromWatchlist(Long.parseLong(principal.getId()), id);
        return ResponseEntity.noContent().build();
    }

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
