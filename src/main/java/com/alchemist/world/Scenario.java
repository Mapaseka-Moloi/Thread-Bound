package com.alchemist.world;

public class Scenario {
    private final String id;
    private final String description;
    private final String yesChoiceText;
    private final String noChoiceText;
    private final int impact;
    private final int reversibility;
    private final int personalCost;

    public Scenario(String id, String description, String yesChoiceText, String noChoiceText,
                    int impact, int reversibility, int personalCost) {
        this.id = id;
        this.description = description;
        this.yesChoiceText = yesChoiceText;
        this.noChoiceText = noChoiceText;
        this.impact = impact;
        this.reversibility = reversibility;
        this.personalCost = personalCost;
    }

    public String getDescription() { return description; }
    public String getYesChoiceText() { return yesChoiceText; }
    public String getNoChoiceText() { return noChoiceText; }
    public int getImpact() { return impact; }
    public int getReversibility() { return reversibility; }
    public int getPersonalCost() { return personalCost; }

    public int getSeverity() {
        return impact + reversibility + personalCost;
    }
}