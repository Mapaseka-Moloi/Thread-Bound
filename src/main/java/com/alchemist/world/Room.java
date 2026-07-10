package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Room {
    private String name;
    protected int x, y, width, height;
    private List<Scenario> scenarios;
    private boolean triggered = false;

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
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public void markTriggered() {
        triggered = true;
    }

    public abstract MoralityScore.Type getType();

    public abstract void draw(GraphicsContext gc);

    protected void drawPerson(GraphicsContext gc, Color skin, Color robe) {
        double px = x + width / 2.0;
        double py = y + height / 2.0;

        gc.setFill(robe);
        gc.fillRect(px - 12, py - 10, 24, 40);

        gc.setFill(skin);
        gc.fillOval(px - 10, py - 32, 20, 20);
    }
}