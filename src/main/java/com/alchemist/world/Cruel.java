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

        // shattered square
        gc.setFill(Color.web("#e53935"));
        gc.fillRect(x + width/2 - 18, y + 80, 36, 36);
        gc.setStroke(Color.web("#3a0a0a"));
        gc.setLineWidth(3);
        gc.strokeLine(x + width/2, y + 80, x + width/2 - 20, y + 60);
        gc.strokeLine(x + width/2, y + 80, x + width/2 + 24, y + 65);
        gc.strokeLine(x + width/2, y + 116, x + width/2 - 18, y + 138);
        gc.strokeLine(x + width/2, y + 116, x + width/2 + 22, y + 140);
        gc.strokeLine(x + width/2 - 18, y + 98, x + width/2 - 38, y + 90);
        gc.strokeLine(x + width/2 + 18, y + 98, x + width/2 + 40, y + 105);
        gc.setFill(Color.web("#e53935"));
        gc.fillRect(x + width/2 - 38, y + 82, 14, 14);
        gc.fillRect(x + width/2 + 28, y + 70, 12, 12);
        gc.fillRect(x + width/2 - 28, y + 130, 10, 10);
        gc.fillRect(x + width/2 + 24, y + 126, 13, 13);
    }

    @Override
    public void drawExpanded(GraphicsContext gc, int W, int H) {
        // dark wasteland
        gc.setFill(Color.web("#1a0505"));
        gc.fillRect(0, 0, W, H);

        // cracked ground
        gc.setFill(Color.web("#2d0a0a"));
        gc.fillRect(0, H/2 - 40, W, 80);

        // crack lines on ground
        gc.setStroke(Color.web("#e53935"));
        gc.setLineWidth(1.5);
        gc.strokeLine(50, H/2 - 20, 150, H/2 + 10);
        gc.strokeLine(150, H/2 + 10, 200, H/2 - 30);
        gc.strokeLine(200, H/2, 280, H/2 + 20);

        // dead trees
        gc.setStroke(Color.web("#4a1010"));
        gc.setLineWidth(5);
        gc.strokeLine(80, 420, 80, 200);
        gc.strokeLine(80, 280, 50, 240);
        gc.strokeLine(80, 300, 110, 260);

        gc.strokeLine(160, 430, 160, 230);
        gc.strokeLine(160, 290, 130, 250);

        // embers / sparks
        gc.setFill(Color.web("#ff5722"));
        gc.fillOval(220, 300, 8, 8);
        gc.fillOval(240, 260, 6, 6);
        gc.fillOval(190, 340, 5, 5);

        drawRiver(gc);
        drawNPC(gc, Color.web("#e53935"));
    }
}