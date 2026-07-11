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

        // open hand symbol
        gc.setFill(Color.web("#f5c5a3"));
        gc.fillRoundRect(x + width/2 - 22, y + 100, 44, 30, 10, 10);
        gc.fillRoundRect(x + width/2 - 20, y + 72, 10, 32, 6, 6);
        gc.fillRoundRect(x + width/2 - 8, y + 64, 10, 38, 6, 6);
        gc.fillRoundRect(x + width/2 + 4, y + 66, 10, 36, 6, 6);
        gc.fillRoundRect(x + width/2 + 16, y + 70, 10, 32, 6, 6);
        gc.fillRoundRect(x + width/2 - 32, y + 84, 12, 24, 6, 6);
    }

    @Override
    public void drawExpanded(GraphicsContext gc, int W, int H) {
        // green meadow
        gc.setFill(Color.web("#2d5a27"));
        gc.fillRect(0, 0, W, H);

        // ground path
        gc.setFill(Color.web("#8B6914"));
        gc.fillRect(0, H/2 - 40, W, 80);

        // trees left side
        gc.setFill(Color.web("#1a3a1a"));
        gc.fillOval(60, 80, 60, 60);
        gc.fillOval(90, 140, 20, 60);
        gc.setFill(Color.web("#4caf50"));
        gc.fillOval(55, 70, 70, 70);

        gc.setFill(Color.web("#1a3a1a"));
        gc.fillOval(100, 360, 60, 60);
        gc.fillOval(120, 400, 20, 60);
        gc.setFill(Color.web("#4caf50"));
        gc.fillOval(95, 350, 70, 70);

        // flowers
        gc.setFill(Color.web("#ffeb3b"));
        gc.fillOval(160, 200, 10, 10);
        gc.fillOval(200, 350, 10, 10);
        gc.fillOval(250, 180, 10, 10);

        drawRiver(gc);
        drawNPC(gc, Color.web("#4caf50"));
    }
}