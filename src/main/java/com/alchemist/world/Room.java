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

    // river obstacle
    protected double riverX = 320;
    protected double riverY = 200;
    protected double riverW = 40;
    protected double riverH = 200;

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

    public boolean isTriggered() {
        return triggered;
    }

    public void markTriggered() {
        triggered = true;
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