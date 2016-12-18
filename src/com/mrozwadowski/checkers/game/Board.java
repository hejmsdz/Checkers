package com.mrozwadowski.checkers.game;

/**
 * Represents a square checkerboard, internally stored as a matrix of {@link Field} objects.
 *
 * Created by rozwad on 15.12.16.
 */
public class Board {
    private int size;
    private Field fields[][];

    Board(int size) {
        this.size = size;
        fields = new Field[size][size];
        initializeFields();
    }

    public int getSize() {
        return size;
    }

    /**
     * Populates the matrix with black and white fields.
     */
    private void initializeFields() {
        int i, j;
        for (i=0; i<size; i++) {
            for (j=0; j<size; j++) {
                fields[i][j] = new Field(i, j);
            }
        }
    }

    public Field getFieldAt(int row, int column) {
        if (row > size || column > size) {
            throw new IllegalArgumentException("Field coordinates are out of range");
        }
        return fields[row][column];
    }

    void placePawn(int row, int column, Pawn pawn) {
        getFieldAt(row, column).setPawn(pawn);
    }
}
