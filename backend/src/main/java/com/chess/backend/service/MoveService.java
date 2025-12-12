package com.chess.backend.service;

import com.chess.backend.entity.Move;
import com.chess.backend.repository.MoveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MoveService {

    private final MoveRepository moveRepository;

    public List<Move> getMovesByGameId(Long gameId) {
        return moveRepository.findByGameIdOrderByMoveNumberAsc(gameId);
    }

    public Move saveMove(Move move) {
        return moveRepository.save(move);
    }
}