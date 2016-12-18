package com.mrozwadowski.checkers.ui;

import com.mrozwadowski.checkers.game.Board;
import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Pawn;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;

/**
 * Draws a board on a Pane.
 *
 * Created by rozwad on 17.12.16.
 */
public class BoardDrawer {
    private Board board;
    private Pane pane;
    private Label label;

    private double borderWidth = 6;
    private double boardSize;
    private double fieldSize;

    public BoardDrawer(Board board, Pane pane, Label label) {
        this.board = board;
        this.pane = pane;
        this.label = label;
        boardSize = board.getSize();
        fieldSize = (pane.getMinWidth() - 2 * borderWidth) / boardSize;
    }

    public void draw() {
        double halfFieldSize = fieldSize/2;

        ObservableList<Node> objects = pane.getChildren();
        objects.clear();

        double x;
        double y = (boardSize - 1) * fieldSize + borderWidth;

        for (int i=0; i<boardSize; i++) {
            x = borderWidth;
            for (int j=0; j<boardSize; j++) {
                Field field = board.getFieldAt(i, j);
                Rectangle square = new Rectangle(fieldSize, fieldSize);
                square.relocate(x, y);

                square.getStyleClass().addAll("field", field.isBlack() ? "black" : "white");
                final String coords = field.userFriendlyCoordinates();
                square.setOnMouseEntered(event -> label.setText(coords));
                square.setOnMouseExited(event -> label.setText(""));
                objects.add(square);

                if (field.hasPawn()) {
                    Node pawn = drawPawn(field.getPawn(), x+halfFieldSize, y+halfFieldSize);
                    objects.add(pawn);
                }

                x += fieldSize;
            }
            y -= fieldSize;
        }
    }

    private Node drawPawn(Pawn pawn, double x, double y) {
        Circle circle = new Circle(fieldSize * 0.3);
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.getStyleClass().addAll("pawn", pawn.isBlack() ? "black" : "white");
        circle.setMouseTransparent(true);
        return circle;
    }
}
