package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Room {
    private String name;
    protected int width, height;
    private List<String> scenarios;

    public Room(String name) {
        this.name = name;
        scenarios = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<String> getScenarios() {
        return Collections.unmodifiableList(scenarios);
    }

    public void addScenario(String scenario) {
        scenarios.add(scenario);
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public abstract void draw(GraphicsContext gc);
}
