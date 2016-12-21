package com.mrozwadowski.checkers;

import com.mrozwadowski.checkers.errors.IllegalMoveException;
import com.mrozwadowski.checkers.events.FieldClickHandler;
import com.mrozwadowski.checkers.events.GameEventListener;
import com.mrozwadowski.checkers.game.*;
import com.mrozwadowski.checkers.players.ComputerPlayer;
import com.mrozwadowski.checkers.players.HumanPlayer;
import com.mrozwadowski.checkers.players.Player;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Controller implements GameEventListener {
    @FXML
    private BorderPane root;
    @FXML
    private Pane boardPane;
    @FXML
    private Label fieldName;
    @FXML
    private Label turnName;
    @FXML
    private ListView<Label> gameLog;

    private Game game;

    private double borderWidth = 6;
    private double boardSize;
    private double fieldSize;

    private FieldClickHandler fieldClicker;

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
        gameLog.scrollTo(item);
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
                square.setUserData(field);
                square.relocate(x, y);

                square.getStyleClass().addAll("field", field.isBlack() ? "black" : "white");

                if (fieldClicker != null) {
                    square.setOnMouseClicked(fieldClicker);
                }
                square.setOnMouseEntered(event -> fieldName.setText(field.userFriendlyCoordinates()+" ["+field.getRow()+","+field.getColumn()+"]"+" ["+field.getPawn()+"]"));
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
    public void pawnMoved(Pawn pawn, MoveSequence move) {
        double animationTime = 500;
        Field source = move.getSource();

        Node node = pawns.get(pawn);
        double x = node.getLayoutX();
        double y = node.getLayoutY();

        List<Pawn> captures = move.getCaptures();
        int numCaptures = captures.size();

        final Timeline timeline = new Timeline();
        int n = 0;
        for (Field target: move.getFields()) {
            if (n == 0) {
                n++;
                continue;
            }

            int di = target.getRow() - source.getRow();
            int dj = target.getColumn() - source.getColumn();

            double newX = x + dj * fieldSize;
            double newY = y - di * fieldSize;

            final KeyValue kvX = new KeyValue(node.layoutXProperty(), newX, Interpolator.EASE_BOTH);
            final KeyValue kvY = new KeyValue(node.layoutYProperty(), newY, Interpolator.EASE_BOTH);
            final KeyFrame kf = new KeyFrame(Duration.millis(n * animationTime), kvX, kvY);
            timeline.getKeyFrames().add(kf);

            if (n <= numCaptures) {
                Pawn captured = captures.get(n - 1);
                Node disappear = pawns.get(captured);

                final KeyValue kvO1 = new KeyValue(disappear.opacityProperty(), 1, Interpolator.EASE_BOTH);
                final KeyFrame kfO1 = new KeyFrame(Duration.millis((n-1) * animationTime), kvO1);

                final KeyValue kvO = new KeyValue(disappear.opacityProperty(), 0, Interpolator.EASE_BOTH);
                final KeyFrame kfO = new KeyFrame(Duration.millis(n * animationTime), kvO);

                timeline.getKeyFrames().addAll(kfO, kfO1);
            }
            n++;
        }

        timeline.setOnFinished(event -> {
            game.getSemaphore().release();
        });

        try {
            game.getSemaphore().acquire();
        } catch (InterruptedException e) {
            game.getSemaphore().release();
            System.out.println("WTF");
        }

        Platform.runLater(() -> {
            logMessage(move.toString());
            node.toFront();
            timeline.play();
        });
    }

    @Override
    public void pawnCrowned(Pawn pawn) {
        logMessage("Crowned!");

        Node node = pawns.get(pawn);
        node.getStyleClass().add("crowned");
    }

    @Override
    public void turnChanged(Color color) {
        Platform.runLater(() -> {
            Player player = game.getPlayer(color);
            turnName.setText(player.getPlayerName());
            turnName.getStyleClass().setAll("pawn", color == Color.BLACK ? "black" : "white");
        });
    }

    public void newGame(ActionEvent event) {

        //Player player1 = new HumanPlayer("Andrzej", this);
        Player player1 = new ComputerPlayer();
        //Player computer2 = new ComputerPlayer();
        Player player2 = new HumanPlayer("Bartek", this);

        game = new Game(8, 3, player1, player2);
        game.setListener(this);

        fieldClicker = new FieldClickHandler(player1 instanceof HumanPlayer?(HumanPlayer) player1:null, player2 instanceof HumanPlayer?(HumanPlayer) player2:null);

        drawBoard();
        game.start();
        logMessage("Game started");
    }

    public void setTheme1(ActionEvent event) {
        root.getStyleClass().setAll("root", "theme1");
    }

    public void setTheme2(ActionEvent event) {
        root.getStyleClass().setAll("root", "theme2");
    }

    public void setTheme3(ActionEvent event) {
        root.getStyleClass().setAll("root", "theme3");
    }

}
