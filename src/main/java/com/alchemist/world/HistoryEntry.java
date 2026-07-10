package com.alchemist.world;

public class HistoryEntry {
    private final MoralityScore.Type type;
    private final Scenario scenario;

    public HistoryEntry(MoralityScore.Type type, Scenario scenario) {
        this.type = type;
        this.scenario = scenario;
    }

    public MoralityScore.Type getType() { return type; }
    public Scenario getScenario() { return scenario; }
}