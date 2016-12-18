package com.mrozwadowski.checkers.ui;

import com.mrozwadowski.checkers.events.GameEventListener;
import com.mrozwadowski.checkers.game.Board;
import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Pawn;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;

/**
 * Draws a board on a Pane.
 *
 * Created by rozwad on 17.12.16.
 */
public class BoardDrawer implements GameEventListener {

    private Board board;
    private Pane pane;
    private Label label;

    private double borderWidth = 6;
    private double boardSize;
    private double fieldSize;

    private HashMap<Pawn, Circle> pawns;

    public BoardDrawer(Board board, Pane pane, Label label) {
        this.board = board;
        this.pane = pane;
        this.label = label;

        boardSize = board.getSize();
        fieldSize = (pane.getMinWidth() - 2 * borderWidth) / boardSize;

        pawns = new HashMap<>();
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
                final int i1 = i;
                final int j1 = j;
                square.setOnMouseEntered(event -> label.setText(coords + " ["+i1+","+j1+"]"));
                square.setOnMouseExited(event -> label.setText(""));
                objects.add(square);

                if (field.hasPawn()) {
                    createPawn(field.getPawn(), x+halfFieldSize, y+halfFieldSize);
                }

                x += fieldSize;
            }
            y -= fieldSize;
        }

        for (Circle circle: pawns.values()) {
            objects.add(circle);
        }
    }

    private Circle createPawn(Pawn pawn, double x, double y) {
        Circle circle = new Circle(fieldSize * 0.3);
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.getStyleClass().addAll("pawn", pawn.isBlack() ? "black" : "white");
        circle.setMouseTransparent(true);

        pawns.put(pawn, circle);

        return circle;
    }

    @Override
    public void pawnMoved(Pawn pawn, Field source, Field target) {
        Node node = pawns.get(pawn);
        double x = node.getLayoutX();
        double y = node.getLayoutY();

        int di = target.getRow() - source.getRow();
        int dj = target.getColumn() - source.getColumn();

        final Timeline timeline = new Timeline();
        final KeyValue kvX = new KeyValue(node.layoutXProperty(), x + dj*fieldSize, Interpolator.EASE_BOTH);
        final KeyValue kvY = new KeyValue(node.layoutYProperty(), y - di*fieldSize, Interpolator.EASE_BOTH);
        final KeyFrame kf = new KeyFrame(Duration.millis(500), kvX, kvY);
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

    @Override
    public void pawnCaptured(Pawn pawn) {

    }
}
