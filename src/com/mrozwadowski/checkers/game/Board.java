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

    public Board(Color colors[][]) {
        size = colors.length;
        fields = new Field[size][size];

        for (int i=0; i<size; i++) {
            for (int j=0; j<size; j++) {
                Color color = colors[size-i-1][j];
                fields[i][j] = new Field(i, j);
                if (color != null) {
                    fields[i][j].setPawn(new Pawn(color, fields[i][j]));
                }
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

    public List<MoveSequence> findPossibleCaptures(Field source, Pawn attacker, Board aux, MoveSequence initial) {
        if (initial == null) {
            initial = new MoveSequence(getFieldAt(source.getRow(), source.getColumn()));
        }
        List<MoveSequence> captures = new ArrayList<>();
        boolean crowned = attacker.isCrowned();

        for (Field neighbor: aux.getNeighbors(source)) {
            int i = neighbor.getRow();
            int j = neighbor.getColumn();
            int di = i - source.getRow();
            int dj = j - source.getColumn();
            Field end;

            if (crowned) {
                Field field = neighbor;
                Pawn captured = null;
                do {
                    if (field.hasPawn() && captured == null) {
                        if (field.getPawn().getColor() != attacker.getColor()) {
                            captured = field.getPawn();
                        }
                    } else if (!field.hasPawn()) {
                        if (captured != null) {
                            addCapture(captures, initial, aux, captured.getField(), field, attacker);
                        }
                    } else {
                        break;
                    }

                    field = aux.getFieldAt(i += di, j += dj);
                } while (field != null);
            } else if (neighbor.hasPawn() && neighbor.getPawn().getColor() != attacker.getColor()) {
                i += di;
                j += dj;
                end = getFieldAt(i, j); // original board
                if (end != null && !end.hasPawn()) {
                    addCapture(captures, initial, aux, neighbor, end, attacker);
                }
            }

        }

        return captures;
    }

    private void addCapture(List<MoveSequence> moves, MoveSequence initial, Board aux, Field neighbor, Field end, Pawn attacker) {
        MoveSequence move = new MoveSequence(initial, end);
        move.addCapture(getFieldAt(neighbor.getRow(), neighbor.getColumn()).getPawn());
        moves.add(move);

        // try to extend a capture by searching a board with the victim pawn removed
        Board boardCopy = new Board(aux);
        Field neighborCopy = boardCopy.getFieldAt(neighbor.getRow(), neighbor.getColumn());
        Field endCopy = boardCopy.getFieldAt(end.getRow(), end.getColumn());
        neighborCopy.setPawn(null);
        boardCopy.dump();

        List<MoveSequence> cont = findPossibleCaptures(endCopy, attacker, boardCopy, move);
        if (!cont.isEmpty()) {
            moves.addAll(cont);
        }
    }

    public List<MoveSequence> findAllPossibleCaptures(Color attacker) {
        List<MoveSequence> captures = new ArrayList<>();
        int i, j;
        for (i=0; i<size; i++) {
            for (j=0; j<size; j++) {
                Field field = getFieldAt(i, j);
                if (field.hasPawn() && field.getPawn().getColor() == attacker) {
                    captures.addAll(findPossibleCaptures(field, field.getPawn(), this, null));
                }
            }
        }
        return captures;
    }

    public List<MoveSequence> findPossibleMoves(Field source, boolean findCaptures) {
        List<MoveSequence> moves;
        Pawn pawn = source.getPawn();
        if (findCaptures) {
            moves = findPossibleCaptures(source, pawn, this, null);
            if (moves.size() > 0) {
                return moves;
            }
        } else {
            moves = new ArrayList<>();
        }


        for (Field neighbor: getNeighbors(source)) {
            int i = neighbor.getRow();
            int j = neighbor.getColumn();
            int di = i - source.getRow();
            int dj = j - source.getColumn();
            if (pawn.isCrowned()) {
                Field field = neighbor;
                do {
                    if (field.hasPawn()) {
                        break;
                    } else {
                        moves.add(new MoveSequence(source, field));
                    }

                    field = getFieldAt(i += di, j += dj);
                } while (field != null);
            } else if (!neighbor.hasPawn() && (di < 0 && pawn.isBlack() || di > 0 && !pawn.isBlack())) {
                moves.add(new MoveSequence(source, neighbor));
            }
        }

        return moves;
    }

    public void dump() {
        System.out.print("{");
        for (int i=size-1; i>=0; i--) {
            System.out.print("  {");
            for (int j=0; j<size; j++) {
                Pawn p = getFieldAt(i, j).getPawn();
                System.out.print((p == null ? "null" : p.getColor()) + ", \t");
            }
            System.out.println("},");
        }
        System.out.println("}");
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
