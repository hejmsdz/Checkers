package com.mrozwadowski.checkers.game;

/**
 * Represents a player's pawn.
 *
 * Created by rozwad on 15.12.16.
 */
public class Pawn {
    private Color color;

    Pawn(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public boolean isBlack() {
        return color == Color.BLACK;
    }
}
