package com.alchemist.gamewindow;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player {
    public double x, y;
    public double angle = -Math.PI / 2;
    public double speed = 3.0;
    public double turnSpeed = 0.04;

    public boolean up, down, left, right;

    // chosen at the avatar-select screen
    public Color color = Color.CYAN;

    // jumping
    public boolean isJumping = false;
    private double jumpVelocity = 0;
    private double gravity = 0.4;
    private double groundY;
    private double zOffset = 0; // vertical lift during jump

    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        this.groundY = y;
    }

    public void setGround(double groundY) {
        this.groundY = groundY;
        this.y = groundY;
        this.zOffset = 0;
    }

    public void jump() {
        if (!isJumping) {
            isJumping = true;
            jumpVelocity = -10;
        }
    }

    public double getVisualY() {
        return y + zOffset;
    }

    // Getter for the jump offset
    public double getZOffset() {
        return zOffset;
    }

    public void update() {
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

        // jumping arc
        if (isJumping) {
            jumpVelocity += gravity;
            zOffset += jumpVelocity;

            if (zOffset >= 0) {
                zOffset = 0;
                isJumping = false;
                jumpVelocity = 0;
            }
        }
    }

    public void draw(GraphicsContext gc) {
        gc.save();
        gc.translate(x, getVisualY());
        gc.rotate(Math.toDegrees(angle));

        gc.setFill(color);
        gc.fillPolygon(
                new double[]{0, -15, 15},
                new double[]{-20, 10, 10},
                3
        );

        gc.restore();

        // Draw the shadow after restoring the transform
        if (isJumping) {
            gc.setFill(Color.color(0, 0, 0, 0.3));
            gc.fillOval(x - 10, y - 5, 20, 10);
        }
    }
}