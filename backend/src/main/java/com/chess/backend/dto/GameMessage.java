package com.chess.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameMessage {
    private String type; // MOVE, GAME_START, GAME_END, RESIGN, DRAW_OFFER
    private Long gameId;
    private Long playerId;
    private String from;
    private String to;
    private String piece;
    private String boardState;
    private String currentTurn;
    private String message;
}