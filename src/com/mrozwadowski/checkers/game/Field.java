package com.mrozwadowski.checkers.game;

/**
 * Represents a single field of a checkerboard and a {@link Pawn} standing on it, if any.
 *
 * Created by rozwad on 15.12.16.
 */
class Field {
    private int row, column;
    private Pawn pawn;
    private Color color;

    Field(int row, int column) {
        this.row = row;
        this.column = column;
        this.color = (row + column) % 2 == 1 ? Color.BLACK : Color.WHITE;
        this.pawn = null;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Pawn getPawn() {
        return pawn;
    }

    void setPawn(Pawn pawn) {
        this.pawn = pawn;
    }

    public Color getColor() {
        return color;
    }

    public String userFriendlyCoordinates() {
        return "" + (char)('A' + column) + (row + 1);
    }
}
