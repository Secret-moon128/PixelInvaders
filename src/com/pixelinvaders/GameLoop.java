package com.pixelinvaders;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class GameLoop extends AnimationTimer {

    private final GraphicsContext gc;
    private final double W, H;
    private final Set<KeyCode> keys;

    private Player player;
    private ArrayList<Enemy> enemies;
    private ArrayList<Bullet> bullets;
    private ArrayList<Particle> particles;
    private ScoreManager scoreManager;
    private HUD hud;

    private GameState state = GameState.IDLE;
    private int level, enemyDir, enemyMoveTimer, enemyShootTimer;
    private double time;

    public GameLoop(GraphicsContext gc, double w, double h, Set<KeyCode> keys) {
        this.gc = gc; W = w; H = h; this.keys = keys;
        scoreManager = new ScoreManager();
        hud = new HUD(gc, W);
    }

    public void startGame() {
        level = 1; enemyDir = 1; enemyMoveTimer = 0; enemyShootTimer = 0; time = 0;
        bullets = new ArrayList<>(); particles = new ArrayList<>();
        player = new Player(W / 2, H - 60);
        scoreManager.reset();
        spawnEnemies();
        state = GameState.PLAYING;
    }

    private void spawnEnemies() {
        enemies = new ArrayList<>();
        int cols = 8, rows = Math.min(3 + level / 2, 5);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                EnemyType type = (r == 0 && rows > 2) ? EnemyType.BOSS
                        : (r < 2)              ? EnemyType.ELITE
                        : EnemyType.GRUNT;
                enemies.add(new Enemy(40 + c * 52, 60 + r * 40, type));
            }
        }
    }

    @Override
    public void handle(long now) {
        gc.setFill(Color.web("#0a0a1a"));
        gc.fillRect(0, 0, W, H);

        if (state == GameState.IDLE)     { drawTitle(); return; }
        if (state == GameState.GAME_OVER){ drawGameOver(); return; }
        if (state == GameState.PAUSED)   { drawPaused(); return; }

        time += 0.05;
        update();
        render();
    }

    private void update() {
        if (keys.contains(KeyCode.LEFT))  player.moveLeft(W);
        if (keys.contains(KeyCode.RIGHT)) player.moveRight(W);
        if (keys.contains(KeyCode.SPACE) && player.canShoot()) {
            bullets.add(new Bullet(player.getX(), player.getY() - 14, 8, true));
            player.shoot();
            spawnBurst(player.getX(), player.getY() - 16, Color.web("#7fd4ff"), 3);
        }
        player.tick();

        bullets.forEach(Bullet::update);
        bullets.removeIf(b -> b.isOffScreen(H));

        enemyShootTimer++;
        int shootInterval = Math.max(12, 40 - level * 3);
        if (enemyShootTimer >= shootInterval) {
            enemyShootTimer = 0;
            ArrayList<Enemy> alive = new ArrayList<>();
            for (Enemy e : enemies) if (e.isAlive()) alive.add(e);
            if (!alive.isEmpty()) {
                Enemy shooter = alive.get((int)(Math.random() * alive.size()));
                bullets.add(new Bullet(shooter.getX(), shooter.getY() + 10, 3 + level * 0.4, false));
            }
        }

        enemyMoveTimer++;
        int moveInterval = Math.max(6, 22 - level * 2);
        if (enemyMoveTimer >= moveInterval) {
            enemyMoveTimer = 0;
            double maxX = -999, minX = 9999;
            for (Enemy e : enemies) { if (!e.isAlive()) continue; maxX = Math.max(maxX, e.getX()); minX = Math.min(minX, e.getX()); }
            boolean drop = false;
            if (enemyDir > 0 && maxX > W - 30) { drop = true; enemyDir = -1; }
            if (enemyDir < 0 && minX < 30)     { drop = true; enemyDir = 1; }
            for (Enemy e : enemies) {
                if (!e.isAlive()) continue;
                e.moveBy(enemyDir * 12, drop ? 18 : 0);
                if (e.getY() > H - 40) { state = GameState.GAME_OVER; return; }
            }
        }

        enemies.forEach(e -> e.tick(time + e.getX() * 0.01));

        Iterator<Bullet> bi = bullets.iterator();
        while (bi.hasNext()) {
            Bullet b = bi.next();
            if (b.isPlayerBullet()) {
                for (Enemy e : enemies) {
                    if (!e.isAlive()) continue;
                    if (b.hits(e.getX(), e.getY(), e.getWidth(), e.getHeight())) {
                        bi.remove();
                        spawnBurst(e.getX(), e.getY(), colorFor(e.getType()), 5);
                        if (e.hit()) {
                            scoreManager.add(e.getType().getPoints() * level);
                            spawnBurst(e.getX(), e.getY(), colorFor(e.getType()), 10);
                        }
                        break;
                    }
                }
            } else {
                if (!player.isInvincible() && b.hits(player.getX(), player.getY(), player.getWidth(), player.getHeight())) {
                    bi.remove();
                    spawnBurst(player.getX(), player.getY(), Color.web("#ff4444"), 8);
                    player.hit();
                    if (player.isDead()) { state = GameState.GAME_OVER; return; }
                }
            }
        }

        boolean anyAlive = false;
        for (Enemy e : enemies) if (e.isAlive()) { anyAlive = true; break; }
        if (!anyAlive) { level++; bullets.clear(); enemyDir = 1; spawnEnemies(); }

        particles.forEach(Particle::update);
        particles.removeIf(Particle::isDead);
    }

    private Color colorFor(EnemyType t) {
        return switch (t) {
            case BOSS  -> Color.web("#ff6a00");
            case ELITE -> Color.web("#c070ff");
            default    -> Color.web("#40c8ff");
        };
    }

    private void spawnBurst(double x, double y, Color col, int n) {
        for (int i = 0; i < n; i++) {
            double a = Math.random() * Math.PI * 2;
            double s = (Math.random() * 0.6 + 0.4) * 3.5;
            particles.add(new Particle(x, y, Math.cos(a)*s, Math.sin(a)*s - 1, col, 2.5 + Math.random()*2));
        }
    }

    private void render() {
        // enemies
        for (Enemy e : enemies) {
            if (!e.isAlive()) continue;
            double x = e.getX(), y = e.getY() + Math.sin(e.getAnimOff()) * 2;
            switch (e.getType()) {
                case BOSS -> { gc.setFill(Color.web("#ff6a00")); gc.fillRect(x-12,y-8,24,6); gc.fillRect(x-8,y-12,16,10); gc.fillRect(x-4,y-14,8,4); gc.setFill(Color.web("#ffaa44")); gc.fillRect(x-3,y-10,6,4); }
                case ELITE -> { gc.setFill(Color.web("#c070ff")); gc.fillRect(x-10,y-6,20,4); gc.fillRect(x-6,y-10,12,8); gc.setFill(Color.web("#e0a0ff")); gc.fillRect(x-3,y-8,6,4); }
                default -> { gc.setFill(Color.web("#40c8ff")); gc.fillRect(x-8,y-5,16,4); gc.fillRect(x-5,y-8,10,6); gc.setFill(Color.web("#80e8ff")); gc.fillRect(x-2,y-6,4,3); }
            }
            if (e.getHp() > 1) {
                gc.setFill(e.getType()==EnemyType.BOSS ? Color.web("#ff6a00") : Color.web("#c070ff"));
                for (int i = 0; i < e.getHp(); i++) gc.fillRect(x - 6 + i*6, y + 10, 4, 2);
            }
        }

        // bullets
        for (Bullet b : bullets) {
            if (b.isPlayerBullet()) {
                gc.setFill(Color.web("#7fd4ff")); gc.fillRect(b.getX()-1.5, b.getY(), 3, 12);
                gc.setFill(Color.WHITE); gc.fillRect(b.getX()-0.5, b.getY(), 1, 8);
            } else {
                gc.setFill(Color.web("#ff4444")); gc.fillRect(b.getX()-1.5, b.getY(), 3, 8);
            }
        }

        // player
        if (player.isVisible()) {
            double px = player.getX(), py = player.getY();
            gc.setFill(Color.web("#7fd4ff")); gc.fillRect(px-14,py,28,5); gc.fillRect(px-9,py-6,18,8); gc.fillRect(px-4,py-10,8,6);
            gc.setFill(Color.WHITE); gc.fillRect(px-2,py-8,4,4);
            gc.setFill(Color.web("#ff8844")); gc.fillRect(px-10,py+5,4,4); gc.fillRect(px+6,py+5,4,4);
        }

        // particles
        for (Particle p : particles) {
            gc.setGlobalAlpha(p.getAlpha());
            gc.setFill(p.getColor());
            gc.fillRect(p.getX(), p.getY(), p.getSize(), p.getSize());
        }
        gc.setGlobalAlpha(1.0);

        hud.draw(scoreManager, level, player.getLives());
    }

    private void drawTitle() {
        gc.setFill(Color.web("#7fd4ff")); gc.setFont(Font.font("Monospace", 26));
        gc.fillText("PIXEL INVADERS", W/2 - 140, 180);
        gc.setFont(Font.font("Monospace", 13)); gc.setFill(Color.web("#4a8aaa"));
        gc.fillText("← → move    space shoot    p pause", W/2 - 130, 220);
        gc.setFill(Color.web("#7fd4ff88")); gc.fillText("press ENTER to start", W/2 - 80, 300);
    }

    private void drawGameOver() {
        gc.setFill(Color.web("#ff4444")); gc.setFont(Font.font("Monospace", 26));
        gc.fillText("GAME OVER", W/2 - 100, 200);
        gc.setFont(Font.font("Monospace", 14)); gc.setFill(Color.web("#7fd4ff"));
        gc.fillText("score: " + scoreManager.getScore(), W/2 - 50, 240);
        gc.setFill(Color.web("#aaaaaa")); gc.fillText("press ENTER to restart", W/2 - 90, 290);
    }

    private void drawPaused() {
        gc.setFill(Color.web("#ffffff88")); gc.setFont(Font.font("Monospace", 22));
        gc.fillText("PAUSED", W/2 - 60, H/2);
        gc.setFont(Font.font("Monospace", 13)); gc.fillText("press P to continue", W/2 - 75, H/2 + 30);
    }

    public void togglePause() {
        if (state == GameState.PLAYING) state = GameState.PAUSED;
        else if (state == GameState.PAUSED) state = GameState.PLAYING;
    }

    public void onEnter() {
        if (state == GameState.IDLE || state == GameState.GAME_OVER) startGame();
    }

    public GameState getState() { return state; }
}