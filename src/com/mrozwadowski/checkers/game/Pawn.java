package com.mrozwadowski.checkers.game;

/**
 * Represents a player's pawn.
 *
 * Created by rozwad on 15.12.16.
 */
public class Pawn {
    private Color color;
    private Field field;
    private boolean crowned;

    Pawn(Color color, Field field) {
        this.color = color;
        this.field = field;
    }

    public Color getColor() {
        return color;
    }

    public boolean isBlack() {
        return color == Color.BLACK;
    }

    public Field getField() {
        return field;
    }

    public boolean isCrowned() {
        return crowned;
    }

    public void setCrowned(boolean crowned) {
        this.crowned = crowned;
    }

    public void moveTo(Field target) {
        assert !target.hasPawn();
        target.setPawn(this);
        field.setPawn(null);
        field = target;
    }


}
