package com.pixelinvaders;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class GameApp extends Application {

    @Override
    public void start(Stage stage) {
        double W = 480, H = 520;
        Canvas canvas = new Canvas(W, H);

        Set<KeyCode> keys = new HashSet<>();
        GameLoop loop = new GameLoop(canvas.getGraphicsContext2D(), W, H, keys);

        StackPane root = new StackPane(canvas);
        root.setStyle("-fx-background-color: #0a0a1a;");

        Scene scene = new Scene(root, W, H);
        scene.setFill(Color.web("#0a0a1a"));

        scene.setOnKeyPressed(e -> {
            keys.add(e.getCode());
            if (e.getCode() == KeyCode.P)      loop.togglePause();
            if (e.getCode() == KeyCode.ENTER)  loop.onEnter();
        });
        scene.setOnKeyReleased(e -> keys.remove(e.getCode()));

        stage.setTitle("Pixel Invaders");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        loop.start();
        canvas.requestFocus();
    }
}