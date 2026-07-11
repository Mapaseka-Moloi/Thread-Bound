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

        // mask symbol
        gc.setFill(Color.web("#ff9800"));
        gc.fillOval(x + width/2 - 36, y + 60, 72, 80);
        gc.setFill(Color.web("#2a1a00"));
        gc.fillOval(x + width/2 - 26, y + 78, 20, 14);
        gc.fillOval(x + width/2 + 6, y + 78, 20, 14);
        gc.fillRoundRect(x + width/2 - 16, y + 114, 32, 6, 4, 4);
        gc.setStroke(Color.web("#2a1a00"));
        gc.setLineWidth(2.5);
        gc.strokeLine(x + width/2, y + 60, x + width/2, y + 140);
        gc.setFill(Color.web("#e65100"));
        gc.fillRect(x + width/2, y + 60, 36, 80);
        gc.setFill(Color.web("#2a1a00"));
        gc.fillOval(x + width/2 + 6, y + 78, 20, 14);
        gc.fillRoundRect(x + width/2, y + 114, 16, 6, 4, 4);
    }

    @Override
    public void drawExpanded(GraphicsContext gc, int W, int H) {
        // foggy swamp
        gc.setFill(Color.web("#1a1200"));
        gc.fillRect(0, 0, W, H);

        // murky ground
        gc.setFill(Color.web("#2a1a00"));
        gc.fillRect(0, H/2 - 40, W, 80);

        // fog patches
        gc.setFill(Color.color(1, 0.6, 0, 0.08));
        gc.fillOval(50, 100, 200, 100);
        gc.fillOval(100, 350, 180, 90);
        gc.fillOval(200, 200, 150, 80);

        // twisted trees
        gc.setStroke(Color.web("#4a3000"));
        gc.setLineWidth(5);
        gc.strokeLine(70, 430, 90, 200);
        gc.strokeLine(90, 260, 60, 210);
        gc.strokeLine(90, 280, 120, 240);
        gc.strokeLine(90, 300, 70, 310);

        gc.strokeLine(170, 420, 185, 220);
        gc.strokeLine(185, 270, 155, 230);
        gc.strokeLine(185, 300, 210, 260);

        // glowing eyes in the dark
        gc.setFill(Color.web("#ff9800"));
        gc.fillOval(230, 180, 8, 6);
        gc.fillOval(245, 180, 8, 6);
        gc.fillOval(210, 380, 8, 6);
        gc.fillOval(225, 380, 8, 6);

        drawRiver(gc);
        drawNPC(gc, Color.web("#ff9800"));
    }
}