package com.chess.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvitationMessage {
    private String type; // INVITE, ACCEPT, DECLINE
    private Long fromUserId;
    private String fromUsername;
    private Long toUserId;
    private String toUsername;
    private Long gameId;
}