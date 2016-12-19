package com.mrozwadowski.checkers.game;

import com.mrozwadowski.checkers.errors.IllegalMoveException;
import com.mrozwadowski.checkers.events.GameEventListener;
import com.mrozwadowski.checkers.players.Player;

/**
 * Represents a game in progress and its state.
 *
 * Created by rozwad on 15.12.16.
 */
public class Game {
    private Board board;
    private Color turn;
    private Player blackPlayer, whitePlayer;
    private GameEventListener listener;

    public Game(int boardSize, int pawnRows, Player blackPlayer, Player whitePlayer) {
        if (boardSize < 5) {
            throw new IllegalArgumentException("Minimum board size is 5");
        }
        if (pawnRows*2 >= boardSize) {
            throw new IllegalArgumentException("Pawns must take less than half of the board");
        }

        board = new Board(boardSize);
        placePawns(pawnRows);

        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
        blackPlayer.setGame(this);
        whitePlayer.setGame(this);

        turn = Color.WHITE;
        whitePlayer.myTurn();
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

    public Player getPlayer(Color color) {
        return color == Color.BLACK ? blackPlayer : whitePlayer;
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

    public void move(Field source, Field target) throws IllegalMoveException {
        if (!source.hasPawn()) {
            throw new IllegalMoveException("Source field must have a pawn on it!");
        }
        Pawn pawn = source.getPawn();
        if (pawn.getColor() != turn) {
            throw new IllegalMoveException("It's not your turn!");
        }
        if (target.hasPawn()) {
            throw new IllegalMoveException("Target field must be empty!");
        }
        int di = target.getRow() - source.getRow();
        int dj = target.getColumn() - source.getColumn();
        if (Math.abs(di) != Math.abs(dj)) {
            throw new IllegalMoveException("Moves must be diagonal!");
        }

        source.setPawn(target.getPawn());
        target.setPawn(pawn);

        listener.pawnMoved(pawn, source, target);

        switchTurn();
    }

    private void switchTurn() {
        if (turn == Color.BLACK) {
            turn = Color.WHITE;
            whitePlayer.myTurn();
        } else {
            turn = Color.BLACK;
            blackPlayer.myTurn();
        }
    }
}
