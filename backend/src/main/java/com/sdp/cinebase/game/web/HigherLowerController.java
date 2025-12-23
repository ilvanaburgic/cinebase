package com.sdp.cinebase.game.web;

import com.sdp.cinebase.game.dto.HigherLowerQuestionDto;
import com.sdp.cinebase.game.dto.LeaderboardEntryDto;
import com.sdp.cinebase.game.model.GameScore;
import com.sdp.cinebase.game.repo.GameScoreRepository;
import com.sdp.cinebase.game.service.HigherLowerService;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game/higher-lower")
public class HigherLowerController {

    private final HigherLowerService service;
    private final GameScoreRepository gameScoreRepository;
    private final UserRepository userRepository;

    public HigherLowerController(HigherLowerService service, GameScoreRepository gameScoreRepository, UserRepository userRepository) {
        this.service = service;
        this.gameScoreRepository = gameScoreRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/questions")
    public ResponseEntity<List<HigherLowerQuestionDto>> getQuestions(
            @RequestParam(defaultValue = "10") int count
    ) {
        List<HigherLowerQuestionDto> questions = service.getRandomQuestions(count);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        List<LeaderboardEntryDto> leaderboard = gameScoreRepository.findLeaderboard()
                .stream()
                .map(entry -> new LeaderboardEntryDto(entry.getUsername(), entry.getBestScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }

    @PostMapping("/submit-score")
    public ResponseEntity<Void> submitScore(@RequestBody ScoreSubmission submission, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GameScore gameScore = new GameScore(user, submission.score());
        gameScoreRepository.save(gameScore);

        return ResponseEntity.ok().build();
    }

    public record ScoreSubmission(Integer score) {}
}
