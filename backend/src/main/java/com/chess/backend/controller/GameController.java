package com.chess.backend.controller;

import com.chess.backend.entity.Game;
import com.chess.backend.entity.Move;
import com.chess.backend.entity.User;
import com.chess.backend.service.GameService;
import com.chess.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<?> createGame(@RequestBody Map<String, Long> request) {
        try {
            Long whitePlayerId = request.get("whitePlayerId");
            Long blackPlayerId = request.get("blackPlayerId");

            User whitePlayer = userService.findById(whitePlayerId)
                    .orElseThrow(() -> new RuntimeException("White player not found"));
            User blackPlayer = userService.findById(blackPlayerId)
                    .orElseThrow(() -> new RuntimeException("Black player not found"));

            Game game = gameService.createGame(whitePlayer, blackPlayer);
            return ResponseEntity.ok(game);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<?> getGame(@PathVariable Long gameId) {
        return gameService.findById(gameId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/player/{playerId}")
    public ResponseEntity<List<Game>> getPlayerGames(@PathVariable Long playerId) {
        return ResponseEntity.ok(gameService.findGamesByPlayer(playerId));
    }

    @GetMapping("/player/{playerId}/active")
    public ResponseEntity<?> getActiveGame(@PathVariable Long playerId) {
        User player = userService.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        return gameService.findActiveGameByPlayer(player)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{gameId}/moves")
    public ResponseEntity<List<Move>> getGameMoves(@PathVariable Long gameId) {
        return ResponseEntity.ok(gameService.getGameMoves(gameId));
    }

    @PostMapping("/{gameId}/end")
    public ResponseEntity<?> endGame(@PathVariable Long gameId, @RequestBody Map<String, Long> request) {
        Long winnerId = request.get("winnerId");
        Game game = gameService.endGame(gameId, winnerId);
        return ResponseEntity.ok(game);
    }
}