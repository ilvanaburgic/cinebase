package com.sdp.cinebase.game.repo;

import com.sdp.cinebase.game.model.HigherLowerQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HigherLowerQuestionRepository extends JpaRepository<HigherLowerQuestion, Long> {

    @Query(value = "SELECT * FROM higher_lower_questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<HigherLowerQuestion> findRandomQuestions(int limit);

    @Query(value = "SELECT metric FROM higher_lower_questions GROUP BY metric ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    String findRandomMetric();

    @Query(value = "SELECT metric FROM higher_lower_questions GROUP BY metric", nativeQuery = true)
    List<String> findAllMetrics();

    @Query(value = """
        SELECT * FROM higher_lower_questions
        WHERE metric = :metric
        AND id IN (
            SELECT MIN(id) FROM higher_lower_questions
            WHERE metric = :metric
            GROUP BY tmdb_id
        )
        ORDER BY RANDOM()
        LIMIT :limit
        """, nativeQuery = true)
    List<HigherLowerQuestion> findRandomQuestionsByMetric(String metric, int limit);
}
