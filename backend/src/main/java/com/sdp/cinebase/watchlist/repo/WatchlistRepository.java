package com.sdp.cinebase.watchlist.repo;

import com.sdp.cinebase.watchlist.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUser_IdOrderByAddedAtDesc(Long userId);

    boolean existsByUser_IdAndTmdbIdAndMediaType(Long userId, Long tmdbId, String mediaType);
}
