package com.chess.backend.repository;

import com.chess.backend.entity.Game;
import com.chess.backend.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MoveRepository extends JpaRepository<Move, Long> {

    List<Move> findByGameOrderByMoveNumberAsc(Game game);

    List<Move> findByGameIdOrderByMoveNumberAsc(Long gameId);

    int countByGame(Game game);
}