package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Deceit extends Room {
    public Deceit(String name) {
        super(name);
    }

    @Override
    public MoralityScore.Type getType() {
        return MoralityScore.Type.DECEIT;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.web("#2a1a00"));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.web("#ff9800"));
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);

        // Mask outer face shape
        gc.setFill(Color.web("#ff9800"));
        gc.fillOval(x + width/2 - 36, y + 60, 72, 80);

        // Mask eyes — hollow cutouts
        gc.setFill(Color.web("#2a1a00"));
        gc.fillOval(x + width/2 - 26, y + 78, 20, 14);
        gc.fillOval(x + width/2 + 6, y + 78, 20, 14);

        // Mouth slit — thin and unreadable
        gc.setFill(Color.web("#2a1a00"));
        gc.fillRoundRect(x + width/2 - 16, y + 114, 32, 6, 4, 4);

        // Split line down the middle — two faces
        gc.setStroke(Color.web("#2a1a00"));
        gc.setLineWidth(2.5);
        gc.strokeLine(x + width/2, y + 60, x + width/2, y + 140);

        // Right half slightly different shade
        gc.setFill(Color.web("#e65100"));
        gc.fillRect(x + width/2, y + 60, 36, 80);

        // Re-draw right eye and mouth on top
        gc.setFill(Color.web("#2a1a00"));
        gc.fillOval(x + width/2 + 6, y + 78, 20, 14);
        gc.fillRoundRect(x + width/2, y + 114, 16, 6, 4, 4);
    }
}