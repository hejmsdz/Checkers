package com.mrozwadowski.checkers.events;

import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Pawn;

/**
 * Handles events related to the gameplay.
 *
 * Created by rozwad on 18.12.16.
 */
public interface GameEventListener {
    void pawnMoved(Pawn pawn, Field source, Field target);

    void pawnCaptured(Pawn pawn);
}
