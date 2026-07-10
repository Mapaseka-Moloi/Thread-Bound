package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;

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
}