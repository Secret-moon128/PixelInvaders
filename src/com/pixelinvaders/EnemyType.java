package com.pixelinvaders;

public enum EnemyType {
    GRUNT(10, 1),
    ELITE(15, 2),
    BOSS(30, 3);

    private final int points;
    private final int maxHp;

    EnemyType(int points, int maxHp) {
        this.points = points;
        this.maxHp = maxHp;
    }

    public int getPoints() { return points; }
    public int getMaxHp()  { return maxHp; }
}