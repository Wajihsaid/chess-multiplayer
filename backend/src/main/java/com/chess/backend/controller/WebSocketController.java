package com.chess.backend.controller;

import com.chess.backend.dto.GameMessage;
import com.chess.backend.dto.InvitationMessage;
import com.chess.backend.entity.Game;
import com.chess.backend.entity.User;
import com.chess.backend.service.GameService;
import com.chess.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;
    private final GameService gameService;

    // Connexion d'un utilisateur
    @MessageMapping("/user.connect")
    public void userConnect(@Payload Long userId) {
        userService.setOnline(userId, true);
        // Notifier tous les utilisateurs qu'un nouveau joueur est connecté
        messagingTemplate.convertAndSend("/topic/users", userService.getOnlineUsers());
    }

    // Déconnexion d'un utilisateur
    @MessageMapping("/user.disconnect")
    public void userDisconnect(@Payload Long userId) {
        userService.setOnline(userId, false);
        messagingTemplate.convertAndSend("/topic/users", userService.getOnlineUsers());
    }

    // Envoyer une invitation
    @MessageMapping("/invitation.send")
    public void sendInvitation(@Payload InvitationMessage invitation) {
        invitation.setType("INVITE");
        // Envoyer l'invitation au joueur cible
        messagingTemplate.convertAndSend(
                "/topic/invitation/" + invitation.getToUserId(),
                invitation
        );
    }

    // Répondre à une invitation
    @MessageMapping("/invitation.respond")
    public void respondToInvitation(@Payload InvitationMessage response) {
        if ("ACCEPT".equals(response.getType())) {
            // Créer la partie
            User whitePlayer = userService.findById(response.getFromUserId())
                    .orElseThrow(() -> new RuntimeException("Player not found"));
            User blackPlayer = userService.findById(response.getToUserId())
                    .orElseThrow(() -> new RuntimeException("Player not found"));

            Game game = gameService.createGame(whitePlayer, blackPlayer);
            response.setGameId(game.getId());

            // Notifier les deux joueurs que la partie commence
            GameMessage gameStart = new GameMessage();
            gameStart.setType("GAME_START");
            gameStart.setGameId(game.getId());
            gameStart.setBoardState(game.getBoardState());
            gameStart.setCurrentTurn("WHITE");

            messagingTemplate.convertAndSend("/topic/game/" + game.getId(), gameStart);
        }

        // Envoyer la réponse à l'inviteur
        messagingTemplate.convertAndSend(
                "/topic/invitation/" + response.getFromUserId(),
                response
        );
    }

    // Jouer un coup
    @MessageMapping("/game.move")
    public void makeMove(@Payload GameMessage move) {
        // Sauvegarder le coup en base
        Game game = gameService.makeMove(
                move.getGameId(),
                move.getPlayerId(),
                move.getFrom(),
                move.getTo(),
                move.getPiece(),
                move.getBoardState()
        );

        // Préparer le message de réponse
        move.setType("MOVE");
        move.setCurrentTurn(game.getCurrentTurn());

        // Diffuser le coup aux deux joueurs
        messagingTemplate.convertAndSend("/topic/game/" + move.getGameId(), move);
    }

    // Fin de partie
    @MessageMapping("/game.end")
    public void endGame(@Payload GameMessage message) {
        Long winnerId = null;
        if (message.getPlayerId() != null) {
            winnerId = message.getPlayerId();
        }

        gameService.endGame(message.getGameId(), winnerId);
        message.setType("GAME_END");

        messagingTemplate.convertAndSend("/topic/game/" + message.getGameId(), message);
    }

    // Abandonner
    @MessageMapping("/game.resign")
    public void resignGame(@Payload GameMessage message) {
        // Le joueur qui abandonne perd
        Game game = gameService.findById(message.getGameId())
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Long winnerId;
        if (game.getWhitePlayer().getId().equals(message.getPlayerId())) {
            winnerId = game.getBlackPlayer().getId();
        } else {
            winnerId = game.getWhitePlayer().getId();
        }

        gameService.endGame(message.getGameId(), winnerId);

        message.setType("GAME_END");
        message.setMessage("Player resigned");

        messagingTemplate.convertAndSend("/topic/game/" + message.getGameId(), message);
    }
}