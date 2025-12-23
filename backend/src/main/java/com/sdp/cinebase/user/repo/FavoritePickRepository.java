package com.sdp.cinebase.user.repo;

import com.sdp.cinebase.user.model.FavoritePick;
import com.sdp.cinebase.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritePickRepository extends JpaRepository<FavoritePick, Long> {

    List<FavoritePick> findByUser(User user);

    boolean existsByUser(User user);
}
