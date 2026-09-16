package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Honest extends Room {
    public Honest(String name) {
        super(name);
    }

    @Override
    public MoralityScore.Type getType() {
        return MoralityScore.Type.HONEST;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.web("#0a1628"));
        gc.fillRect(x, y, width, height);

        gc.setStroke(Color.web("#1565c0"));
        gc.setLineWidth(3);
        gc.strokeRect(x, y, width, height);

        // eye symbol
        gc.setFill(Color.web("#e8f0fe"));
        gc.fillOval(x + width/2 - 40, y + 80, 80, 44);
        gc.setFill(Color.web("#1565c0"));
        gc.fillOval(x + width/2 - 18, y + 87, 36, 30);
        gc.setFill(Color.web("#0a1628"));
        gc.fillOval(x + width/2 - 9, y + 94, 18, 16);
        gc.setFill(Color.web("#ffffff"));
        gc.fillOval(x + width/2 - 4, y + 96, 7, 6);
        gc.setStroke(Color.web("#1565c0"));
        gc.setLineWidth(2.5);
        gc.strokeArc(x + width/2 - 40, y + 80, 80, 44, 0, 180, javafx.scene.shape.ArcType.OPEN);
        gc.strokeArc(x + width/2 - 40, y + 80, 80, 44, 180, 180, javafx.scene.shape.ArcType.OPEN);
        gc.setLineWidth(2);
        gc.strokeLine(x + width/2 - 30, y + 82, x + width/2 - 34, y + 72);
        gc.strokeLine(x + width/2 - 10, y + 79, x + width/2 - 10, y + 68);
        gc.strokeLine(x + width/2 + 10, y + 79, x + width/2 + 10, y + 68);
        gc.strokeLine(x + width/2 + 30, y + 82, x + width/2 + 34, y + 72);
    }

    @Override
    public void drawExpanded(GraphicsContext gc, int W, int H) {
        // open sky, clean and clear
        gc.setFill(Color.web("#0a1628"));
        gc.fillRect(0, 0, W, H);

        // stars
        gc.setFill(Color.WHITE);
        int[][] stars = {{80,60},{200,40},{350,80},{500,30},{620,70},
                {150,150},{420,120},{560,160},{100,300},{700,200}};
        for (int[] s : stars) {
            gc.fillOval(s[0], s[1], 4, 4);
        }

        // moon
        gc.setFill(Color.web("#e8f0fe"));
        gc.fillOval(650, 40, 60, 60);
        gc.setFill(Color.web("#0a1628"));
        gc.fillOval(665, 35, 60, 60);

        // stone path
        gc.setFill(Color.web("#2a3a5a"));
        gc.fillRect(0, H/2 - 40, W, 80);

        // stone tiles on path
        gc.setStroke(Color.web("#1565c0"));
        gc.setLineWidth(1);
        for (int i = 0; i < W; i += 60) {
            gc.strokeRect(i, H/2 - 35, 55, 30);
            gc.strokeRect(i + 30, H/2 + 5, 55, 30);
        }

        drawRiver(gc);
        drawNPC(gc, Color.web("#1565c0"));
    }
}