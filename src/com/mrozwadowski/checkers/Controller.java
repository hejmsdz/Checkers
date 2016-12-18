package com.mrozwadowski.checkers;

import com.mrozwadowski.checkers.game.Game;
import com.mrozwadowski.checkers.ui.BoardDrawer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Pane boardPane;
    @FXML
    private Label fieldName;

    Game game;

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

    }

    public void newGame(ActionEvent event) {
        Game game = new Game(8, 3);

        BoardDrawer drawer = new BoardDrawer(game.getBoard(), boardPane, fieldName);
        drawer.draw();
    }

}
