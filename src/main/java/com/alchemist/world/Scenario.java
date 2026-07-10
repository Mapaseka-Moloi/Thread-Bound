package com.alchemist.world;

public class Scenario {
    private String id;
    private String description;
    private int impact;
    private int reversibility;
    private int personalCost;


    public Scenario(String id, String description, int impact, int reversibility, int personalCost) {
        this.id = id;
        this.description = description;
        this.impact = impact;
        this.reversibility = reversibility;
        this.personalCost = personalCost;

    }

    public String getDescription() {
        return description;
    }
    public int getImpact() {
        return impact;
    }
    public int getReversibility() {
        return reversibility;
    }
    public int getPersonalCost() {
        return personalCost;
    }
    public  int getSeverity() {
        return impact + reversibility + personalCost;
    }
}
