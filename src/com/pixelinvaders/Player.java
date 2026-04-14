package com.pixelinvaders;

public class Player {
    private double x, y;
    private int lives;
    private double speed;
    private int invincibleTimer;
    private int shootCooldown;

    private static final double WIDTH = 28, HEIGHT = 20;

    public Player(double startX, double startY) {
        this.x = startX; this.y = startY;
        this.lives = 3; this.speed = 4.5;
    }

    public void moveLeft(double screenW) {
        x = Math.max(WIDTH / 2, x - speed);
    }

    public void moveRight(double screenW) {
        x = Math.min(screenW - WIDTH / 2, x + speed);
    }

    public boolean canShoot() { return shootCooldown <= 0; }

    public void shoot()       { shootCooldown = 10; }

    public void tick() {
        if (shootCooldown > 0) shootCooldown--;
        if (invincibleTimer > 0) invincibleTimer--;
    }

    public void hit() {
        if (invincibleTimer > 0) return;
        lives--;
        invincibleTimer = 120;
    }

    public boolean isInvincible()  { return invincibleTimer > 0; }
    public boolean isVisible()     { return invincibleTimer <= 0 || (invincibleTimer / 6) % 2 == 0; }
    public boolean isDead()        { return lives <= 0; }

    public double getX()    { return x; }
    public double getY()    { return y; }
    public int getLives()   { return lives; }
    public double getWidth(){ return WIDTH; }
    public double getHeight(){ return HEIGHT; }
}