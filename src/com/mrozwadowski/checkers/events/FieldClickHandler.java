package com.mrozwadowski.checkers.events;

import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Pawn;
import com.mrozwadowski.checkers.players.HumanPlayer;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * Handles mouse events on fields, delivering them to {@link HumanPlayer} objects.
 *
 * Created by rozwad on 19.12.16.
 */
public class FieldClickHandler implements EventHandler<MouseEvent> {
    private HumanPlayer blackPlayer, whitePlayer, movingPlayer;

    public FieldClickHandler(HumanPlayer blackPlayer, HumanPlayer whitePlayer) {
        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
    }

    @Override
    public void handle(MouseEvent event) {
        Object source = event.getSource();
        if (!(source instanceof Node))
            return;

        Object data = ((Node)source).getUserData();
        if (!(data instanceof Field))
            return;

        Field field = (Field)data;

        if (event.getButton() == MouseButton.PRIMARY) {
            if (movingPlayer != null) {
                movingPlayer.requestMoveEnd(field);
                movingPlayer = null;
            }
        } else {
            if (field.hasPawn()) {
                Pawn pawn = field.getPawn();
                if (pawn.isBlack() && blackPlayer != null) {
                    blackPlayer.requestMoveStart(field);
                    movingPlayer = blackPlayer;
                } else if (!pawn.isBlack() && whitePlayer != null) {
                    whitePlayer.requestMoveStart(field);
                    movingPlayer = whitePlayer;
                }
            }
        }
    }
}
