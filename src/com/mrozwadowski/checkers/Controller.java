package com.mrozwadowski.checkers;

import com.mrozwadowski.checkers.errors.IllegalMoveException;
import com.mrozwadowski.checkers.events.GameEventListener;
import com.mrozwadowski.checkers.game.Field;
import com.mrozwadowski.checkers.game.Game;
import com.mrozwadowski.checkers.game.Pawn;
import com.mrozwadowski.checkers.players.HumanPlayer;
import com.mrozwadowski.checkers.players.Player;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;

public class Controller implements GameEventListener {
    @FXML
    private Pane boardPane;
    @FXML
    private Label fieldName;
    @FXML
    private ListView<Label> gameLog;

    private Game game;

    private double borderWidth = 6;
    private double boardSize;
    private double fieldSize;

    private HumanPlayer moveInProgress;


    private HashMap<Pawn, Circle> pawns;

    public void logMessage(String message) {
        logMessage(message, "");
    }

    public void logMessage(String message, String cssClass) {
        Label item = new Label(message);
        if (!cssClass.isEmpty()) {
            item.getStyleClass().add(cssClass);
        }
        gameLog.getItems().add(item);
    }

    private void drawBoard() {
        boardSize = game.getBoard().getSize();
        fieldSize = (boardPane.getWidth() - 2 * borderWidth) / boardSize;
        double halfFieldSize = fieldSize/2;
        pawns = new HashMap<>();

        ObservableList<Node> objects = boardPane.getChildren();
        objects.clear();
        gameLog.getItems().clear();

        double x;
        double y = (boardSize - 1) * fieldSize + borderWidth;

        for (int i=0; i<boardSize; i++) {
            x = borderWidth;
            for (int j=0; j<boardSize; j++) {
                final Field field = game.getBoard().getFieldAt(i, j);
                Rectangle square = new Rectangle(fieldSize, fieldSize);
                square.relocate(x, y);

                square.getStyleClass().addAll("field", field.isBlack() ? "black" : "white");

                square.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && moveInProgress != null) {
                        moveInProgress.requestMoveEnd(field);
                        moveInProgress = null;
                    } else {
                        if (field.hasPawn()) {
                            Pawn pawn = field.getPawn();
                            Player player = game.getPlayer(pawn.getColor());
                            if (player.getClass() == HumanPlayer.class) {
                                moveInProgress = (HumanPlayer)player;
                                moveInProgress.requestMoveStart(field);
                            }
                        }
                    }
                });
                square.setOnMouseEntered(event -> fieldName.setText(field.userFriendlyCoordinates()));
                square.setOnMouseExited(event -> fieldName.setText(""));
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
        logMessage(source.userFriendlyCoordinates() + " - " + target.userFriendlyCoordinates());

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

    public void newGame(ActionEvent event) {
        Player p1 = new HumanPlayer("Andrzej", this);
        Player p2 = new HumanPlayer("Bartek", this);

        game = new Game(8, 2, p1, p2);
        game.setListener(this);
        drawBoard();
    }

    public void setTheme1(ActionEvent event) {
        boardPane.getStyleClass().setAll("theme1");
    }

    public void setTheme2(ActionEvent event) {
        boardPane.getStyleClass().setAll("theme2");
    }

    public void setTheme3(ActionEvent event) {
        boardPane.getStyleClass().setAll("theme3");
    }

}
