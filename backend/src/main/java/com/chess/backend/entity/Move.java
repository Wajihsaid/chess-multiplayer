package com.chess.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "moves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    private int moveNumber;

    private String fromPosition; // ex: "e2"

    private String toPosition; // ex: "e4"

    private String piece; // ex: "PAWN", "KING"

    private String capturedPiece; // pièce capturée si applicable

    @Column(columnDefinition = "TEXT")
    private String boardStateAfter; // état du plateau après le coup

    private LocalDateTime timestamp = LocalDateTime.now();
}