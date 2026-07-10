package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Ally {
    private final String name;
    private final double x, y;
    private final double size = 30;
    private boolean wasInside = false;

    public Ally(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
    }

    public boolean contains(double px, double py) {
        return px >= x - size/2 && px <= x + size/2 && py >= y - size/2 && py <= y + size/2;
    }

    public boolean wasInsideLastFrame() { return wasInside; }
    public void setWasInside(boolean value) { wasInside = value; }

    public String react(MoralityScore score) {
        int good = score.getScore(MoralityScore.Type.KIND) + score.getScore(MoralityScore.Type.HONEST);
        int bad = score.getScore(MoralityScore.Type.CRUEL) + score.getScore(MoralityScore.Type.DECEIT);

        if (good >= bad) {
            return name + " joins your side. Threadbound is stronger together.";
        } else {
            return name + " hesitates, remembering what you did. Trust takes time to rebuild.";
        }
    }

    public void draw(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillOval(x - size/2, y - size/2, size, size);
    }
}