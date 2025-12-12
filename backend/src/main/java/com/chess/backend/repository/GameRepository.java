package com.chess.backend.repository;

import com.chess.backend.entity.Game;
import com.chess.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByWhitePlayerOrBlackPlayer(User whitePlayer, User blackPlayer);

    @Query("SELECT g FROM Game g WHERE (g.whitePlayer = ?1 OR g.blackPlayer = ?1) AND g.status = 'IN_PROGRESS'")
    Optional<Game> findActiveGameByPlayer(User player);

    List<Game> findByStatus(Game.GameStatus status);

    @Query("SELECT g FROM Game g WHERE (g.whitePlayer.id = ?1 OR g.blackPlayer.id = ?1) ORDER BY g.createdAt DESC")
    List<Game> findGamesByPlayerId(Long playerId);
}