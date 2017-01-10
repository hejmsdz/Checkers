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
    private Spinner<Integer> boardSize, pawnRows;
    private SpinnerValueFactory boardSizeFactory, pawnRowsFactory;

    Controller mainController;

    public void initialize() {
        boardSizeFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 20);
        boardSizeFactory.setValue(8);
        pawnRowsFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 4);
        pawnRowsFactory.setValue(3);

        boardSize.setValueFactory(boardSizeFactory);
        pawnRows.setValueFactory(pawnRowsFactory);
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

        Game game = new Game(boardSizeValue, pawnRowsValue, blackPlayer, whitePlayer);
        mainController.startNewGame(game);
        Stage stage = (Stage) boardSize.getScene().getWindow();
        stage.close();
    }
}
