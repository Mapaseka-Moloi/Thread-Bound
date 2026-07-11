package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Cruel extends Room {
    public Cruel(String name) {
        super(name);
    }

    @Override
    public MoralityScore.Type getType() {
        return MoralityScore.Type.CRUEL;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.web("#3a0a0a"));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.web("#e53935"));
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);

        // Shattered square — center piece
        gc.setFill(Color.web("#e53935"));
        gc.fillRect(x + width/2 - 18, y + 80, 36, 36);

        // Crack lines radiating out
        gc.setStroke(Color.web("#3a0a0a"));
        gc.setLineWidth(3);
        gc.strokeLine(x + width/2, y + 80, x + width/2 - 20, y + 60);
        gc.strokeLine(x + width/2, y + 80, x + width/2 + 24, y + 65);
        gc.strokeLine(x + width/2, y + 116, x + width/2 - 18, y + 138);
        gc.strokeLine(x + width/2, y + 116, x + width/2 + 22, y + 140);
        gc.strokeLine(x + width/2 - 18, y + 98, x + width/2 - 38, y + 90);
        gc.strokeLine(x + width/2 + 18, y + 98, x + width/2 + 40, y + 105);

        // Scattered broken fragments
        gc.setFill(Color.web("#e53935"));
        gc.fillRect(x + width/2 - 38, y + 82, 14, 14);
        gc.fillRect(x + width/2 + 28, y + 70, 12, 12);
        gc.fillRect(x + width/2 - 28, y + 130, 10, 10);
        gc.fillRect(x + width/2 + 24, y + 126, 13, 13);
    }
}