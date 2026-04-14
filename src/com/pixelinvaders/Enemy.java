package com.pixelinvaders;

public class Enemy {
    private double x, y;
    private int hp;
    private boolean alive;
    private final EnemyType type;
    private double animOffset;

    private static final double WIDTH = 24, HEIGHT = 16;

    public Enemy(double x, double y, EnemyType type) {
        this.x = x; this.y = y;
        this.type = type;
        this.hp = type.getMaxHp();
        this.alive = true;
        this.animOffset = Math.random() * Math.PI * 2;
    }

    public void moveBy(double dx, double dy) { x += dx; y += dy; }

    public void tick(double time)            { animOffset = time; }

    public boolean hit() {
        hp--;
        if (hp <= 0) { alive = false; return true; }
        return false;
    }

    public boolean collides(double px, double py, double pw, double ph) {
        return alive
                && Math.abs(x - px) < (WIDTH / 2 + pw / 2)
                && Math.abs(y - py) < (HEIGHT / 2 + ph / 2);
    }

    public double getX()        { return x; }
    public double getY()        { return y; }
    public double getAnimOff()  { return animOffset; }
    public EnemyType getType()  { return type; }
    public boolean isAlive()    { return alive; }
    public int getHp()          { return hp; }
    public double getWidth()    { return WIDTH; }
    public double getHeight()   { return HEIGHT; }
}