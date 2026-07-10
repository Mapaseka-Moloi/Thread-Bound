package com.alchemist.gamewindow;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    double x, y;
    double angle = -Math.PI/2;
    double speed = 3.0;
    double turnSpeed = 0.04;

    boolean up, down, left , right;

    public Player (double x, double y){
        this.x = x;
        this.y = y;
    }

    public void update(){
        if (left) angle -= turnSpeed;
        if (right) angle += turnSpeed;

        if (up) {
            x += Math.cos(angle) * speed;
            y += Math.sin(angle) * speed;
        }

        if (down) {
            x -= Math.cos(angle) * speed;
            y -= Math.sin(angle) * speed;
        }

        x = Math.clamp(x, 20, 800 - 20);
        y = Math.clamp(y, 20, 600 - 20);
    }

    public void draw(GraphicsContext gc){
        gc.save();
        gc.translate(x, y);
        gc.rotate(Math.toDegrees(angle));
        gc.setFill(Color.CYAN);
        gc.fillPolygon(new double[]{0, -15, 15}, new double[]{-20, 10, 10}, 3);
        gc.restore();
    }
}
