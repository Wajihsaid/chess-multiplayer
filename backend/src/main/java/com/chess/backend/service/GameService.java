package com.chess.backend.service;

import com.chess.backend.entity.Game;
import com.chess.backend.entity.Move;
import com.chess.backend.entity.User;
import com.chess.backend.repository.GameRepository;
import com.chess.backend.repository.MoveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;

    // Position initiale du plateau d'échecs
    private static final String INITIAL_BOARD =
            "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";

    public Game createGame(User whitePlayer, User blackPlayer) {
        Game game = new Game();
        game.setWhitePlayer(whitePlayer);
        game.setBlackPlayer(blackPlayer);
        game.setBoardState(INITIAL_BOARD);
        game.setStatus(Game.GameStatus.IN_PROGRESS);
        game.setCurrentTurn("WHITE");
        game.setCreatedAt(LocalDateTime.now());
        game.setUpdatedAt(LocalDateTime.now());

        return gameRepository.save(game);
    }

    public Optional<Game> findById(Long id) {
        return gameRepository.findById(id);
    }

    public Optional<Game> findActiveGameByPlayer(User player) {
        return gameRepository.findActiveGameByPlayer(player);
    }

    public List<Game> findGamesByPlayer(Long playerId) {
        return gameRepository.findGamesByPlayerId(playerId);
    }

    public Game makeMove(Long gameId, Long playerId, String from, String to, String piece, String newBoardState) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        User player = null;
        if (game.getWhitePlayer().getId().equals(playerId)) {
            player = game.getWhitePlayer();
        } else if (game.getBlackPlayer().getId().equals(playerId)) {
            player = game.getBlackPlayer();
        } else {
            throw new RuntimeException("Player not in this game");
        }

        // Sauvegarder le coup
        Move move = new Move();
        move.setGame(game);
        move.setPlayer(player);
        move.setMoveNumber(moveRepository.countByGame(game) + 1);
        move.setFromPosition(from);
        move.setToPosition(to);
        move.setPiece(piece);
        move.setBoardStateAfter(newBoardState);
        move.setTimestamp(LocalDateTime.now());
        moveRepository.save(move);

        // Mettre à jour la partie
        game.setBoardState(newBoardState);
        game.setCurrentTurn(game.getCurrentTurn().equals("WHITE") ? "BLACK" : "WHITE");
        game.setUpdatedAt(LocalDateTime.now());

        return gameRepository.save(game);
    }

    public Game endGame(Long gameId, Long winnerId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        game.setStatus(Game.GameStatus.FINISHED);
        if (winnerId != null) {
            if (game.getWhitePlayer().getId().equals(winnerId)) {
                game.setWinner(game.getWhitePlayer());
            } else {
                game.setWinner(game.getBlackPlayer());
            }
        }
        game.setUpdatedAt(LocalDateTime.now());

        return gameRepository.save(game);
    }

    public List<Move> getGameMoves(Long gameId) {
        return moveRepository.findByGameIdOrderByMoveNumberAsc(gameId);
    }

    public Game updateBoardState(Long gameId, String boardState) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));
        game.setBoardState(boardState);
        game.setUpdatedAt(LocalDateTime.now());
        return gameRepository.save(game);
    }
}