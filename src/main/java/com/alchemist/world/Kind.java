package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Kind extends Room {
    public Kind(String name) {
        super(name);
    }

    @Override
    public MoralityScore.Type getType() {
        return MoralityScore.Type.KIND;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.web("#1a3a1a"));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.web("#4caf50"));
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);

        // Open hand — palm base
        gc.setFill(Color.web("#f5c5a3"));
        gc.fillRoundRect(x + width/2 - 22, y + 100, 44, 30, 10, 10);

        // Five fingers
        gc.fillRoundRect(x + width/2 - 20, y + 72, 10, 32, 6, 6);
        gc.fillRoundRect(x + width/2 - 8, y + 64, 10, 38, 6, 6);
        gc.fillRoundRect(x + width/2 + 4, y + 66, 10, 36, 6, 6);
        gc.fillRoundRect(x + width/2 + 16, y + 70, 10, 32, 6, 6);
        gc.fillRoundRect(x + width/2 - 32, y + 84, 12, 24, 6, 6);
    }
}