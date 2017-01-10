package com.mrozwadowski.checkers.game;

import com.mrozwadowski.checkers.events.GameEventListener;
import com.mrozwadowski.checkers.players.Player;

import java.util.List;
import java.util.concurrent.Semaphore;

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
    private boolean started;
    private boolean over;

    private Semaphore semaphore;
    private Semaphore blackSemaphore, whiteSemaphore;

    public Game(int boardSize, int pawnRows, Player blackPlayer, Player whitePlayer) {
        if (boardSize < 5) {
            throw new IllegalArgumentException("Minimum board size is 5");
        }
        if (pawnRows*2 >= boardSize) {
            throw new IllegalArgumentException("Pawns must take less than half of the board");
        }

        board = new Board(boardSize);
        placePawns(pawnRows);

        blackSemaphore = new Semaphore(1);
        whiteSemaphore = new Semaphore(1);

        try {
            blackSemaphore.acquire();
            whiteSemaphore.acquire();
        } catch (InterruptedException e) {
            System.out.println("WTF");
        }

        this.blackPlayer = blackPlayer;
        this.whitePlayer = whitePlayer;
        blackPlayer.setGame(this, Color.BLACK, blackSemaphore);
        whitePlayer.setGame(this, Color.WHITE, whiteSemaphore);
        semaphore = new Semaphore(1);

        turn = Color.BLACK;
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

    public boolean isOver() {
        return over;
    }

    public Semaphore getSemaphore() {
        return semaphore;
    }

    private Semaphore getPlayerSemaphore(Color color) {
        return color == Color.BLACK ? blackSemaphore : whiteSemaphore;
    }

    /**
     * Places white and black pawns on the edges of the board.
     */
    private void placePawns(int pawnRows) {
        for (int i=0; i<pawnRows; i++) {
            for (int j=i%2; j<board.getSize(); j+=2) {
                board.placePawn(i, j, Color.WHITE);
            }
        }

        for (int i=board.getSize()-pawnRows; i<board.getSize(); i++) {
            for (int j=i%2; j<board.getSize(); j+=2) {
                board.placePawn(i, j, Color.BLACK);
            }
        }
    }

    public void start() {
        if (!started) {
            started = true;
            switchTurn();

            blackPlayer.start();
            whitePlayer.start();
        }
    }

    public void end() {
        gameOver();
    }

    private void gameOver() {
        over = true;
        blackSemaphore.release();
        whiteSemaphore.release();

        blackPlayer.interrupt();
        whitePlayer.interrupt();
    }

    public void move(MoveSequence move) {
        // let's just assume it's legit

        Field source = move.getSource();
        Field target = move.getTarget();
        Pawn pawn = source.getPawn();

        pawn.moveTo(target);

        for (Pawn captured: move.getCaptures()) {
            captured.getField().setPawn(null);
        }

        // pawn can be crowned
        for (Field field: move.getFields()) {
            int row = field.getRow();
            if (pawn.isBlack() && row == 0 || !pawn.isBlack() && row == board.getSize() - 1) {
                pawn.setCrowned(true);
                listener.pawnCrowned(pawn);
            }
        }

        listener.pawnMoved(pawn, move);
        switchTurn();
    }

    private void switchTurn() {
        List<MoveSequence> moves;

        if (turn == Color.BLACK) {
            turn = Color.WHITE;
        } else {
            turn = Color.BLACK;
        }

        moves = board.findAllPossibleMoves(turn);
        if (moves.isEmpty()) {
            gameOver();
            return;
        }
        getPlayer(turn).setMoves(moves);
        getPlayerSemaphore(turn).release();

        if(listener != null) listener.turnChanged(turn);
    }
}
