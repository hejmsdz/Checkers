package com.mrozwadowski.checkers.players;

import com.mrozwadowski.checkers.game.MoveSequence;

import java.util.Random;

/**
 * A bot player which currently performs the longest possible capture or a random move.
 *
 * Created by rozwad on 20.12.16.
 */
public class ComputerPlayer extends Player {
    private Random random;

    public ComputerPlayer() {
        random = new Random();
    }

    public String getPlayerName() {
        return "Computer";
    }

    public MoveSequence move() {
        MoveSequence move = null;
        int maxCaptures = 0;
        for (MoveSequence testMove: moves) {
            if (testMove.getCaptures().size() > maxCaptures) {
                move = testMove;
                maxCaptures = testMove.getCaptures().size();
            }
        }
        if (move == null) {
            int i = random.nextInt(moves.size());
            move = moves.get(i);
        }

        return move;
    }

}
