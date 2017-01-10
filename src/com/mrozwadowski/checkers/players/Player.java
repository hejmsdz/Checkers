package com.mrozwadowski.checkers.players;

import com.mrozwadowski.checkers.game.Color;
import com.mrozwadowski.checkers.game.Game;
import com.mrozwadowski.checkers.game.MoveSequence;

import java.util.List;
import java.util.concurrent.Semaphore;

/**
 *
 *
 * Created by rozwad on 18.12.16.
 */
public abstract class Player extends Thread {
    protected Game game;
    protected Color color;
    protected List<MoveSequence> moves;
    private Semaphore myTurn;

    public final void setGame(Game game, Color color, Semaphore myTurn) {
        this.game = game;
        this.color = color;
        this.myTurn = myTurn;
    }

    public abstract String getPlayerName();
    public abstract MoveSequence move();

    public final void setMoves(List<MoveSequence> moves) {
        this.moves = moves;
    }

    public void run() {
        while (true) {
            try {
                myTurn.acquire();
            } catch (InterruptedException e) {
                myTurn.release();
            }

            if (game.isOver()) {
                break;
            }

            MoveSequence chosenMove = move();

            if (chosenMove == null) {
                break;
            }

            game.move(chosenMove);

            moves = null;
        }

        System.out.println("Goodbye from "+getPlayerName());
    }

}
