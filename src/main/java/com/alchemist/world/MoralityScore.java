package com.alchemist.world;

import java.util.EnumMap;
import java.util.Map;

public class MoralityScore {
    public enum Type { KIND, CRUEL, DECEIT, HONEST }

    private final Map<Type, Integer> tally = new EnumMap<>(Type.class);

    public MoralityScore() {
        for (Type t : Type.values()) tally.put(t, 0);
    }

    public void apply(Type type, Scenario scenario) {
        tally.put(type, tally.get(type) + scenario.getSeverity());
    }

    public int getScore(Type type) {
        return tally.get(type);
    }

    public Type getDominantType() {
        Type dominant = Type.KIND;
        int max = -1;
        for (Map.Entry<Type, Integer> e : tally.entrySet()) {
            if (e.getValue() > max) {
                max = e.getValue();
                dominant = e.getKey();
            }
        }
        return dominant;
    }
}