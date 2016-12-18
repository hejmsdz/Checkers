package com.mrozwadowski.checkers.game;

import com.mrozwadowski.checkers.events.GameEventListener;

/**
 * Represents a game in progress and its state.
 *
 * Created by rozwad on 15.12.16.
 */
public class Game {
    private Board board;
    private GameEventListener listener;

    public Game(int boardSize, int pawnRows) {
        if (boardSize < 5) {
            throw new IllegalArgumentException("Minimum board size is 5");
        }
        if (pawnRows*2 >= boardSize) {
            throw new IllegalArgumentException("Pawns must take less than half of the board");
        }

        board = new Board(boardSize);
        placePawns(pawnRows);
    }

    public Board getBoard() {
        return board;
    }

    public GameEventListener getListener() {
        return listener;
    }

    public void setListener(GameEventListener listener) {
        this.listener = listener;
    }


    /**
     * Places white and black pawns on the edges of the board.
     */
    private void placePawns(int pawnRows) {
        for (int i=0; i<pawnRows; i++) {
            for (int j=i%2; j<board.getSize(); j+=2) {
                board.placePawn(i, j, new Pawn(Color.WHITE));
            }
        }

        for (int i=board.getSize()-pawnRows; i<board.getSize(); i++) {
            for (int j=i%2; j<board.getSize(); j+=2) {
                board.placePawn(i, j, new Pawn(Color.BLACK));
            }
        }
    }

    public void move(int i0, int j0, int i, int j) {
        Field source = board.getFieldAt(i0, j0);
        Field target = board.getFieldAt(i, j);

        Pawn pawn = source.getPawn();
        source.setPawn(target.getPawn());
        target.setPawn(pawn);

        int di = i - i0;
        int dj = j - j0;
        listener.pawnMoved(pawn, source, target);
    }
}
