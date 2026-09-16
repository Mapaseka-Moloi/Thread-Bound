package com.alchemist.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerHistory {
    private final List<HistoryEntry> entries = new ArrayList<>();

    public void record(MoralityScore.Type type, Scenario scenario) {
        entries.add(new HistoryEntry(type, scenario));
    }

    public List<HistoryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public long countOf(MoralityScore.Type type) {
        return entries.stream().filter(e -> e.getType() == type).count();
    }
}