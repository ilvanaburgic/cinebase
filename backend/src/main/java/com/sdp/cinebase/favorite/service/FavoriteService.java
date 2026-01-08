package com.sdp.cinebase.favorite.service;

import com.sdp.cinebase.favorite.dto.AddFavoriteRequest;
import com.sdp.cinebase.favorite.dto.FavoriteResponse;
import com.sdp.cinebase.favorite.model.Favorite;
import com.sdp.cinebase.favorite.repo.FavoriteRepository;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
    }

    public FavoriteResponse addFavorite(Long userId, AddFavoriteRequest request) {
        // Check if already exists
        if (favoriteRepository.existsByUser_IdAndTmdbIdAndMediaType(userId, request.tmdbId(), request.mediaType())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Already in favorites");
        }

        // Fetch user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setTmdbId(request.tmdbId());
        favorite.setMediaType(request.mediaType());
        favorite.setTitle(request.title());
        favorite.setPosterPath(request.posterPath());

        Favorite saved = favoriteRepository.save(favorite);
        return toResponse(saved);
    }

    public List<FavoriteResponse> getUserFavorites(Long userId) {
        return favoriteRepository.findByUser_IdOrderByAddedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void deleteFavorite(Long userId, Long favoriteId) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Favorite not found"));

        // Check ownership
        if (!favorite.getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this favorite");
        }

        favoriteRepository.delete(favorite);
    }

    public boolean isFavorite(Long userId, Long tmdbId, String mediaType) {
        return favoriteRepository.existsByUser_IdAndTmdbIdAndMediaType(userId, tmdbId, mediaType);
    }

    private FavoriteResponse toResponse(Favorite f) {
        return new FavoriteResponse(
                f.getId(),
                f.getTmdbId(),
                f.getMediaType(),
                f.getTitle(),
                f.getPosterPath(),
                f.getAddedAt()
        );
    }
}
