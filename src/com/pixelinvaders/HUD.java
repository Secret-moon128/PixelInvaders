package com.pixelinvaders;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class HUD {
    private final GraphicsContext gc;
    private final double screenW;

    public HUD(GraphicsContext gc, double screenW) {
        this.gc = gc; this.screenW = screenW;
    }

    public void draw(ScoreManager sm, int level, int lives) {
        gc.setFont(Font.font("Monospace", 13));

        gc.setFill(Color.web("#7fd4ff"));
        gc.fillText("score", 16, 20);
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(sm.getScore()), 16, 36);

        gc.setFill(Color.web("#7fd4ff"));
        gc.fillText("level", screenW / 2 - 20, 20);
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(level), screenW / 2 - 8, 36);

        gc.setFill(Color.web("#7fd4ff"));
        gc.fillText("hi", screenW - 80, 20);
        gc.setFill(Color.WHITE);
        gc.fillText(String.valueOf(sm.getHiScore()), screenW - 80, 36);

        gc.setFill(Color.web("#ff6666"));
        String livesStr = "♦".repeat(Math.max(0, lives));
        gc.fillText(livesStr, 16, 52);
    }
}