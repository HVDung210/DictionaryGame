package com.example.dictionary;

import javafx.application.Application;
import javafx.stage.Stage;

public class Game extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

    }

    public static void main(String[] args) {
        launch(args);
    }

    public void playAgain() {
        GameController game = new GameController();
    }
}
