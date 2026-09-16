package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Room {
    protected String name;
    protected int x, y, width, height;
    private List<Scenario> scenarios;
    private boolean triggered = false;

    // river obstacle (moves side to side over time)
    protected double riverBaseX = 320;
    protected double riverX = riverBaseX;
    protected double riverY = 200;
    protected double riverW = 40;
    protected double riverH = 200;
    protected double riverRange = 40; // how far it drifts left/right

    // patrolling guard obstacle (moves up and down)
    protected double guardX = 480;
    protected double guardBaseY = 300;
    protected double guardY = guardBaseY;
    protected double guardRange = 90;
    protected double guardSize = 26;

    // NPC position
    protected double npcX = 650;
    protected double npcY = 270;

    public Room(String name) {
        this.name = name;
        scenarios = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<Scenario> getScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    public void addScenario(Scenario scenario) {
        scenarios.add(scenario);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean contains(double px, double py) {
        return px >= x && px <= x + width
                && py >= y && py <= y + height;
    }

    public double getCenterX() {
        return x + width / 2.0;
    }

    public double getCenterY() {
        return y + height / 2.0;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void markTriggered() {
        triggered = true;
    }

    // called every frame while the player is inside this room, to animate obstacles
    public void updateObstacles(long tick) {
        double t = tick / 30.0;
        riverX = riverBaseX + Math.sin(t) * riverRange;
        guardY = guardBaseY + Math.sin(t * 1.3) * guardRange;
    }

    public boolean playerNearNPC(double px, double py) {
        return Math.abs(px - npcX) < 40
                && Math.abs(py - npcY) < 40;
    }

    public boolean playerOnRiver(double px, double py) {
        return px >= riverX && px <= riverX + riverW
                && py >= riverY && py <= riverY + riverH;
    }

    public boolean playerCrossedRiver(double px, boolean isJumping) {
        return px > riverX + riverW || isJumping;
    }

    public boolean playerHitsGuard(double px, double py) {
        return Math.abs(px - guardX) < guardSize
                && Math.abs(py - guardY) < guardSize;
    }

    // =========================
    // GETTERS
    // =========================

    public double getRiverX() {
        return riverX;
    }

    public double getRiverY() {
        return riverY;
    }

    public double getRiverW() {
        return riverW;
    }

    public double getRiverH() {
        return riverH;
    }

    public double getNpcX() {
        return npcX;
    }

    public double getNpcY() {
        return npcY;
    }

    // =========================

    public abstract MoralityScore.Type getType();

    public abstract void draw(GraphicsContext gc);

    public abstract void drawExpanded(GraphicsContext gc, int W, int H);

    protected void drawRiver(GraphicsContext gc) {
        gc.setFill(Color.web("#1a6fa8"));
        gc.fillRect(riverX, riverY, riverW, riverH);

        gc.setStroke(Color.web("#5bbfea"));
        gc.setLineWidth(1.5);

        for (int i = 0; i < 5; i++) {
            double ly = riverY + 30 + i * 35;
            gc.strokeLine(riverX + 4, ly, riverX + riverW - 4, ly);
        }

        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(riverX - 8, riverY, 8, riverH);
        gc.fillRect(riverX + riverW, riverY, 8, riverH);

        drawGuard(gc);
    }

    protected void drawGuard(GraphicsContext gc) {
        // a roaming shadow figure that patrols the path between the river and the NPC
        gc.setFill(Color.color(0.05, 0.05, 0.08, 0.9));
        gc.fillOval(guardX - guardSize / 2, guardY - guardSize / 2, guardSize, guardSize);

        gc.setFill(Color.web("#e53935"));
        gc.fillOval(guardX - 6, guardY - 4, 5, 5);
        gc.fillOval(guardX + 2, guardY - 4, 5, 5);
    }

    protected void drawNPC(GraphicsContext gc, Color color) {
        gc.setFill(color);
        gc.fillOval(npcX - 18, npcY - 18, 36, 36);

        gc.setFill(Color.web("#f5c5a3"));
        gc.fillOval(npcX - 10, npcY - 32, 20, 20);

        gc.setFill(Color.YELLOW);
        gc.fillRect(npcX - 3, npcY - 56, 6, 16);
        gc.fillOval(npcX - 3, npcY - 36, 6, 6);
    }
}