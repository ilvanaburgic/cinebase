package com.sdp.cinebase.game.repo;

import com.sdp.cinebase.game.model.GameScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameScoreRepository extends JpaRepository<GameScore, Long> {

    @Query("""
        SELECT u.username AS username, MAX(gs.score) AS bestScore
        FROM GameScore gs
        JOIN gs.user u
        GROUP BY u.id, u.username
        HAVING MAX(gs.score) > 0
        ORDER BY MAX(gs.score) DESC
        """)
    List<LeaderboardEntry> findLeaderboard();

    interface LeaderboardEntry {
        String getUsername();
        Integer getBestScore();
    }
}
