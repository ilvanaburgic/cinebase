package com.sdp.cinebase.game.web;

import com.sdp.cinebase.game.dto.HigherLowerQuestionDto;
import com.sdp.cinebase.game.dto.LeaderboardEntryDto;
import com.sdp.cinebase.game.model.GameScore;
import com.sdp.cinebase.game.repo.GameScoreRepository;
import com.sdp.cinebase.game.service.HigherLowerService;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game/higher-lower")
@Tag(name = "Higher/Lower Game", description = "Movie rating comparison game with leaderboard and score tracking")
public class HigherLowerController {

    private final HigherLowerService service;
    private final GameScoreRepository gameScoreRepository;
    private final UserRepository userRepository;

    public HigherLowerController(HigherLowerService service, GameScoreRepository gameScoreRepository, UserRepository userRepository) {
        this.service = service;
        this.gameScoreRepository = gameScoreRepository;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Get game questions", description = "Get random movie pairs for the Higher/Lower game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Questions retrieved successfully")
    })
    @GetMapping("/questions")
    public ResponseEntity<List<HigherLowerQuestionDto>> getQuestions(
            @RequestParam(defaultValue = "10") int count
    ) {
        List<HigherLowerQuestionDto> questions = service.getRandomQuestions(count);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Get leaderboard", description = "Get top 10 players by best score in the Higher/Lower game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Leaderboard retrieved successfully")
    })
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDto>> getLeaderboard() {
        List<LeaderboardEntryDto> leaderboard = gameScoreRepository.findLeaderboard()
                .stream()
                .map(entry -> new LeaderboardEntryDto(entry.getUsername(), entry.getBestScore()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(leaderboard);
    }

    @Operation(summary = "Submit game score", description = "Submit player's score after completing a game")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Score submitted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @SecurityRequirement(name = "bearer-jwt")
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
