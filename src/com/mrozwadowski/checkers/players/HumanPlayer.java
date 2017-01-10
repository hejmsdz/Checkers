package com.mrozwadowski.checkers.players;

import com.mrozwadowski.checkers.Controller;
import com.mrozwadowski.checkers.errors.IllegalMoveException;
import com.mrozwadowski.checkers.game.Color;
import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Game;
import com.mrozwadowski.checkers.game.MoveSequence;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * Makes moves from requested source to target field.
 *
 * Created by rozwad on 18.12.16.
 */
public class HumanPlayer extends Player {
    private String name;
    private Field moveSource, moveTarget;
    private MoveSequence chosenMove;
    private Semaphore moveReady;

    public HumanPlayer(String name) {
        this.name = name;
        moveReady = new Semaphore(1);
        try {
            moveReady.acquire();
        } catch (InterruptedException e) {
            System.out.println("WTF");
        }
    }

    public String getPlayerName() {
        return name;
    }

    public void requestMoveStart(Field field) {
        moveSource = field;
    }

    public void requestMoveEnd(Field field) {
        chosenMove = null;
        moveTarget = field;

        if (moveSource != null && moves != null) {
            for (MoveSequence move: moves) {
                if (move.getSource().equals(moveSource) && move.getTarget().equals(moveTarget)) {
                    if (chosenMove == null || move.getCaptures().size() > chosenMove.getCaptures().size()) {
                        chosenMove = move;
                    }
                }
            }

            if (chosenMove != null) {
                moveReady.release();
            }
        }
    }

    @Override
    public MoveSequence move() {
        try {
            moveReady.acquire();
        } catch (InterruptedException e) {
            moveReady.release();
            return null;
        }
        return chosenMove;
    }
}
