package com.mrozwadowski.checkers.game;

/**
 * Represents a game in progress and its state.
 *
 * Created by rozwad on 15.12.16.
 */
public class Game {
    private Board board;

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
}
