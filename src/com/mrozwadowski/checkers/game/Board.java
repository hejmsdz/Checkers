package com.mrozwadowski.checkers.game;

import java.util.ArrayList;
import java.util.List;

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

    Board(Board other) {
        size = other.size;
        fields = new Field[size][size];
        int i, j;
        for (i=0; i<size; i++) {
            for (j=0; j<size; j++) {
                fields[i][j] = new Field(i, j);
                fields[i][j].setPawn(other.getFieldAt(i, j).getPawn());
            }
        }
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
        if (row < 0 || column < 0 || row >= size || column >= size) {
            return null;
        }
        return fields[row][column];
    }

    private List<Field> getNeighbors(Field field) {
        return getNeighbors(field.getRow(), field.getColumn());
    }

    private List<Field> getNeighbors(int row, int column) {
        ArrayList<Field> neighbors = new ArrayList<>(4);

        int deltas[] = {-1, 1};
        for (int di: deltas) {
            for (int dj: deltas) {
                Field field = getFieldAt(row+di, column+dj);
                if (field != null) {
                    neighbors.add(field);
                }
            }
        }

        return neighbors;
    }

    public List<MoveSequence> findPossibleCaptures(Field source, Color attacker, Board aux, MoveSequence initial) {
        if (initial == null) {
            initial = new MoveSequence(source);
        }
        List<MoveSequence> captures = new ArrayList<>();

        for (Field neighbor: aux.getNeighbors(source)) {
            if (neighbor.hasPawn() && neighbor.getPawn().getColor() != attacker) {
                int i = neighbor.getRow() + neighbor.getRow() - source.getRow();
                int j = neighbor.getColumn() + neighbor.getColumn() - source.getColumn();
                Field end = getFieldAt(i, j); // original board
                if (end == null) {
                    continue;
                }

                if (!end.hasPawn()) {
                    MoveSequence move = new MoveSequence(initial, end);
                    move.addCapture(getFieldAt(neighbor.getRow(), neighbor.getColumn()).getPawn());

                    Board boardCopy = new Board(this);
                    Field neighborCopy = boardCopy.getFieldAt(neighbor.getRow(), neighbor.getColumn());
                    Field endCopy = boardCopy.getFieldAt(i, j);
                    neighborCopy.setPawn(null);

                    List<MoveSequence> cont = findPossibleCaptures(endCopy, attacker, boardCopy, move);
                    if (cont.isEmpty()) {
                        captures.add(move);
                    } else {
                        captures.addAll(cont);
                    }
                }
            }
        }

        return captures;
    }

    public List<MoveSequence> findAllPossibleCaptures(Color attacker) {
        List<MoveSequence> captures = new ArrayList<>();
        int i, j;
        for (i=0; i<size; i++) {
            for (j=0; j<size; j++) {
                Field field = getFieldAt(i, j);
                if (field.hasPawn() && field.getPawn().getColor() == attacker) {
                    captures.addAll(findPossibleCaptures(field, attacker, this, null));
                }
            }
        }
        return captures;
    }

    public List<MoveSequence> findPossibleMoves(Field source, boolean findCaptures) {
        List<MoveSequence> moves;
        Pawn pawn = source.getPawn();
        if (findCaptures) {
            moves = findPossibleCaptures(source, pawn.getColor(), this, null);
            if (moves.size() > 0) {
                return moves;
            }
        } else {
            moves = new ArrayList<>();
        }

        int i = source.getRow();
        int j = source.getColumn();

        for (Field neighbor: getNeighbors(source)) {
            if (!neighbor.hasPawn() && (pawn.isCrowned() || (neighbor.getRow() < source.getRow() && pawn.isBlack()) || (neighbor.getRow() > source.getRow() && !pawn.isBlack()))) {
                moves.add(new MoveSequence(source, neighbor));
            }
        }
        return moves;
    }

    public List<MoveSequence> findAllPossibleMoves(Color pawnColor) {
        // if a capture is possible, it's the only option
        List<MoveSequence> moves = findAllPossibleCaptures(pawnColor);
        if (moves.size() > 0) {
            return moves;
        }

        int i, j;
        for (i=0; i<size; i++) {
            for (j=0; j<size; j++) {
                Field field = getFieldAt(i, j);
                if (field.hasPawn() && field.getPawn().getColor() == pawnColor) {
                    moves.addAll(findPossibleMoves(field, false));
                }
            }
        }
        return moves;
    }

    void placePawn(int row, int column, Color color) {
        Field field = getFieldAt(row, column);
        Pawn pawn = new Pawn(color, field);
        field.setPawn(pawn);
    }
}
