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

        // Eye white — outer almond shape using two arcs via polygon
        gc.setFill(Color.web("#e8f0fe"));
        gc.fillOval(x + width/2 - 40, y + 80, 80, 44);

        // Iris
        gc.setFill(Color.web("#1565c0"));
        gc.fillOval(x + width/2 - 18, y + 87, 36, 30);

        // Pupil
        gc.setFill(Color.web("#0a1628"));
        gc.fillOval(x + width/2 - 9, y + 94, 18, 16);

        // Eye shine
        gc.setFill(Color.web("#ffffff"));
        gc.fillOval(x + width/2 - 4, y + 96, 7, 6);

        // Upper eyelid line
        gc.setStroke(Color.web("#1565c0"));
        gc.setLineWidth(2.5);
        gc.strokeArc(x + width/2 - 40, y + 80, 80, 44, 0, 180, javafx.scene.shape.ArcType.OPEN);

        // Lower eyelid line
        gc.strokeArc(x + width/2 - 40, y + 80, 80, 44, 180, 180, javafx.scene.shape.ArcType.OPEN);

        // Lashes top
        gc.setStroke(Color.web("#1565c0"));
        gc.setLineWidth(2);
        gc.strokeLine(x + width/2 - 30, y + 82, x + width/2 - 34, y + 72);
        gc.strokeLine(x + width/2 - 10, y + 79, x + width/2 - 10, y + 68);
        gc.strokeLine(x + width/2 + 10, y + 79, x + width/2 + 10, y + 68);
        gc.strokeLine(x + width/2 + 30, y + 82, x + width/2 + 34, y + 72);
    }
}