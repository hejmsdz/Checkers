package com.mrozwadowski.checkers.players;

import com.mrozwadowski.checkers.Controller;
import com.mrozwadowski.checkers.errors.IllegalMoveException;
import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Game;

/**
 * Created by rozwad on 18.12.16.
 */
public class HumanPlayer implements Player {
    private String name;
    private Controller controller;
    private Game game;
    private Field moveSource, moveTarget;

    public HumanPlayer(String name, Controller controller) {
        this.name = name;
        this.controller = controller;
    }

    public String getName() {
        return name;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void myTurn() {
        System.out.println("Teraz "+name+" robi ruch.");
    }

    public void requestMoveStart(Field field) {
        System.out.println("starting");
        moveSource = field;
    }

    public void requestMoveEnd(Field field) {
        System.out.println("ending");
        moveTarget = field;

        if (moveSource != null) {
            try {
                game.move(moveSource, moveTarget);
            } catch (IllegalMoveException e) {
                controller.logMessage(e.getMessage(), "error");
            }
        } else {
            controller.logMessage("Right click on the source field and left click on the target field to move.", "error");
        }
    }
}
