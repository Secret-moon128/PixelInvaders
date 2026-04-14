package com.pixelinvaders;

import javafx.scene.paint.Color;

public class Particle {
    private double x, y, vx, vy;
    private double life, maxLife;
    private final Color color;
    private final double size;

    public Particle(double x, double y, double vx, double vy, Color color, double size) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.color = color; this.size = size;
        this.life = this.maxLife = 30 + Math.random() * 20;
    }

    public void update() {
        x += vx; y += vy;
        vy += 0.08;
        life--;
    }

    public boolean isDead()    { return life <= 0; }
    public double getX()       { return x; }
    public double getY()       { return y; }
    public double getSize()    { return size; }
    public Color getColor()    { return color; }
    public double getAlpha()   { return life / maxLife; }
}