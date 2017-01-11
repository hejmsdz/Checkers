package com.mrozwadowski.checkers;

import com.mrozwadowski.checkers.game.Game;
import com.mrozwadowski.checkers.players.ComputerPlayer;
import com.mrozwadowski.checkers.players.HumanPlayer;
import com.mrozwadowski.checkers.players.Player;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class NewGameController {
    @FXML
    private RadioButton whiteHuman, whiteComputer, blackHuman, blackComputer;
    @FXML
    private TextField whiteName, blackName;
    @FXML
    private Spinner<Integer> boardSize, pawnRows, timeLimit;
    @FXML
    private CheckBox timeLimitCheck;
    private SpinnerValueFactory boardSizeFactory, pawnRowsFactory, timeLimitFactory;

    Controller mainController;

    public void initialize() {
        boardSizeFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20, 8);
        pawnRowsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 3);
        timeLimitFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(30, 600, 120, 10);

        boardSize.setValueFactory(boardSizeFactory);
        pawnRows.setValueFactory(pawnRowsFactory);
        timeLimit.setValueFactory(timeLimitFactory);

        boardSize.valueProperty().addListener((obs, oldVal, newVal) -> {
            int max = (int)(Math.ceil(newVal / 2.0) - 1);
            pawnRowsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, max, 3);
            pawnRows.setValueFactory(pawnRowsFactory);
        });
    }

    public void timeLimitChecked(ActionEvent event) {
        timeLimit.setDisable(!timeLimitCheck.isSelected());
    }

    public void startGame(ActionEvent event) {
        int boardSizeValue = (int) boardSizeFactory.getValue();
        int pawnRowsValue = (int) pawnRowsFactory.getValue();
        Player whitePlayer, blackPlayer;
        if (whiteHuman.isSelected()) {
            whitePlayer = new HumanPlayer(whiteName.getText());
        } else {
            whitePlayer = new ComputerPlayer(whiteName.getText());
        }

        if (blackHuman.isSelected()) {
            blackPlayer = new HumanPlayer(blackName.getText());
        } else {
            blackPlayer = new ComputerPlayer(blackName.getText());
        }

        int limit = 0;
        if (timeLimitCheck.isSelected()) {
            limit = (int)timeLimitFactory.getValue();
        }

        Game game;
        try {
            game = new Game(boardSizeValue, pawnRowsValue, blackPlayer, whitePlayer, limit);
        } catch (IllegalArgumentException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
            return;
        }

        mainController.startNewGame(game);
        Stage stage = (Stage) boardSize.getScene().getWindow();
        stage.close();
    }
}
