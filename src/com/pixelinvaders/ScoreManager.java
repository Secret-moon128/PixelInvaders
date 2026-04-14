package com.pixelinvaders;

import java.util.ArrayList;
import java.util.Collections;

public class ScoreManager {
    private int score;
    private int hiScore;
    private final ArrayList<Integer> scoreHistory; // Week 8 — ArrayList

    public ScoreManager() {
        score = 0; hiScore = 0;
        scoreHistory = new ArrayList<>();
    }

    public void add(int points) {
        score += points;
        scoreHistory.add(points); // autoboxing: int → Integer (Week 2)
        if (score > hiScore) hiScore = score;
    }

    public void reset() {
        score = 0;
        scoreHistory.clear();
    }

    public int getScore()   { return score; }
    public int getHiScore() { return hiScore; }
    public int getBestRound() {
        return scoreHistory.isEmpty() ? 0 : Collections.max(scoreHistory); // unboxing
    }
}