package com.sdp.cinebase.watchlist.service;

import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import com.sdp.cinebase.watchlist.dto.AddWatchlistRequest;
import com.sdp.cinebase.watchlist.dto.WatchlistResponse;
import com.sdp.cinebase.watchlist.model.Watchlist;
import com.sdp.cinebase.watchlist.repo.WatchlistRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final UserRepository userRepository;

    public WatchlistService(WatchlistRepository watchlistRepository, UserRepository userRepository) {
        this.watchlistRepository = watchlistRepository;
        this.userRepository = userRepository;
    }

    public WatchlistResponse addToWatchlist(Long userId, AddWatchlistRequest request) {
        // Check if already exists
        if (watchlistRepository.existsByUser_IdAndTmdbIdAndMediaType(userId, request.tmdbId(), request.mediaType())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already in watchlist");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        watchlist.setTmdbId(request.tmdbId());
        watchlist.setMediaType(request.mediaType());
        watchlist.setTitle(request.title());
        watchlist.setPosterPath(request.posterPath());

        Watchlist saved = watchlistRepository.save(watchlist);
        return toResponse(saved);
    }

    public List<WatchlistResponse> getUserWatchlist(Long userId) {
        return watchlistRepository.findByUser_IdOrderByAddedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteFromWatchlist(Long userId, Long watchlistId) {
        Watchlist watchlist = watchlistRepository.findById(watchlistId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Watchlist item not found"));

        // Check ownership
        if (!watchlist.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this item");
        }

        watchlistRepository.delete(watchlist);
    }

    public boolean isInWatchlist(Long userId, Long tmdbId, String mediaType) {
        return watchlistRepository.existsByUser_IdAndTmdbIdAndMediaType(userId, tmdbId, mediaType);
    }

    private WatchlistResponse toResponse(Watchlist w) {
        return new WatchlistResponse(
                w.getId(),
                w.getTmdbId(),
                w.getMediaType(),
                w.getTitle(),
                w.getPosterPath(),
                w.getAddedAt()
        );
    }
}
