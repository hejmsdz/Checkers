package com.mrozwadowski.checkers.players;

import com.mrozwadowski.checkers.game.Game;

/**
 *
 *
 * Created by rozwad on 18.12.16.
 */
public interface Player {
    String getName();
    void setGame(Game game);
    void myTurn();

}
