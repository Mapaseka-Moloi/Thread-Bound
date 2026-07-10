package com.alchemist.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Deceit extends Room {
    public Deceit(String name) {
        super(name);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.ORANGE);
        gc.fillRect(0,0,width,height);
    }
}
