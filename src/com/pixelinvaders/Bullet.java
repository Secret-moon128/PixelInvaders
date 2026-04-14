package com.pixelinvaders;

public class Bullet {
    private double x, y;
    private final double speed;
    private final boolean playerBullet;
    private static final double WIDTH = 4, HEIGHT = 12;

    public Bullet(double x, double y, double speed, boolean playerBullet) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.playerBullet = playerBullet;
    }

    public void update() {
        y += playerBullet ? -speed : speed;
    }

    public boolean isOffScreen(double screenH) {
        return y < -20 || y > screenH + 20;
    }

    public boolean hits(double tx, double ty, double tw, double th) {
        return x > tx - tw/2 && x < tx + tw/2
                && y > ty - th/2 && y < ty + th/2;
    }

    public double getX()          { return x; }
    public double getY()          { return y; }
    public double getWidth()      { return WIDTH; }
    public double getHeight()     { return HEIGHT; }
    public boolean isPlayerBullet() { return playerBullet; }
}