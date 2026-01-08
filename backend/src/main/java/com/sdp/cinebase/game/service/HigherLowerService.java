package com.sdp.cinebase.game.service;

import com.sdp.cinebase.game.dto.HigherLowerQuestionDto;
import com.sdp.cinebase.game.model.HigherLowerQuestion;
import com.sdp.cinebase.game.repo.HigherLowerQuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HigherLowerService {

    private final HigherLowerQuestionRepository repository;

    public HigherLowerService(HigherLowerQuestionRepository repository) {
        this.repository = repository;
    }

    public List<HigherLowerQuestionDto> getRandomQuestions(int count) {
        // Get all available metrics
        List<String> allMetrics = repository.findAllMetrics();
        java.util.Collections.shuffle(allMetrics);

        List<HigherLowerQuestion> result = new java.util.ArrayList<>();
        java.util.Set<Long> usedIds = new java.util.HashSet<>();

        // Generate pairs: for each question, we need 2 items with the SAME metric
        // For 10 questions, we need 20 items (2 per question) organized in pairs
        // Pair 1: [item0, item1] both with same metric
        // Pair 2: [item2, item3] both with same metric (can be different from pair 1)
        // ...
        int itemsNeeded = count * 2;
        int pairsGenerated = 0;
        int metricsIndex = 0;

        while (result.size() < itemsNeeded && metricsIndex < allMetrics.size() * count) {
            String metric = allMetrics.get(metricsIndex % allMetrics.size());
            metricsIndex++;

            // Get candidates with this metric
            List<HigherLowerQuestion> candidates = repository.findRandomQuestionsByMetric(metric, 10);

            // Find 2 unique movies with this metric for one comparison
            int added = 0;
            for (HigherLowerQuestion q : candidates) {
                if (!usedIds.contains(q.getTmdbId())) {
                    result.add(q);
                    usedIds.add(q.getTmdbId());
                    added++;
                    if (added >= 2) {
                        pairsGenerated++;
                        break;
                    }
                }
            }

            // If we couldn't find 2 items for this metric, remove the partial add
            if (added == 1) {
                result.remove(result.size() - 1);
                usedIds.remove(result.get(result.size() - 1).getTmdbId());
            }
        }

        // Ensure we have enough items
        if (result.size() < itemsNeeded) {
            // Fallback: get random pairs without metric constraint
            List<HigherLowerQuestion> fallback = repository.findRandomQuestions(itemsNeeded * 2);
            for (HigherLowerQuestion q : fallback) {
                if (!usedIds.contains(q.getTmdbId()) && result.size() < itemsNeeded) {
                    result.add(q);
                    usedIds.add(q.getTmdbId());
                }
            }
        }

        return result.stream()
            .limit(itemsNeeded)
            .map(q -> new HigherLowerQuestionDto(
                q.getId(),
                q.getMediaType(),
                q.getTmdbId(),
                q.getTitle(),
                q.getPosterPath(),
                q.getMetric(),
                q.getValue()
            ))
            .collect(Collectors.toList());
    }
}
