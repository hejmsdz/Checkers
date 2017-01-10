package com.mrozwadowski.checkers.events;

import com.mrozwadowski.checkers.game.Color;
import com.mrozwadowski.checkers.game.MoveSequence;
import com.mrozwadowski.checkers.game.Pawn;

/**
 * Handles events related to the gameplay.
 *
 * Created by rozwad on 18.12.16.
 */
public interface GameEventListener {
    void pawnMoved(Pawn pawn, MoveSequence move);

    void pawnCrowned(Pawn pawn);

    void turnChanged(Color color);

    void gameOver(Color winner);

    void timeUpdate(int time);
}
