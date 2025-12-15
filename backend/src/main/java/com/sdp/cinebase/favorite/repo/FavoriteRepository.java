package com.sdp.cinebase.favorite.repo;

import com.sdp.cinebase.favorite.model.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUserIdOrderByAddedAtDesc(Long userId);


    boolean existsByUserIdAndTmdbIdAndMediaType(Long userId, Long tmdbId, String mediaType);
}
