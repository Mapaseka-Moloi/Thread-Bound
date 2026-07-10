package com.alchemist.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class World {
    private int width;
    private int height;
    private Position position;
    private Direction currentDirection;
    private List<Room> rooms;

    public World(int width, int height, Position position) {
        this.width = width;
        this.height = height;
        this.position = position;
        currentDirection = Direction.NORTH;
        rooms = new ArrayList<>();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return currentDirection;
    }

    public List<Room> getRooms() {
        return Collections.unmodifiableList(rooms);
    }

    public Position updatePosition(int steps) {
        int newX = position.getX();
        int newY = position.getY();

        Position position = new Position(newX, newY);

        for(int i = 0; i <= steps; i++) {
            switch(currentDirection) {
                case NORTH:
                    newY += i;
                case EAST:
                    newX += i;
                case SOUTH:
                    newY -= i;
                case WEST:
                    newX -= i;
            }
        }

        return position;
    }

    public void updateDirection(boolean turnRight) {
        Direction[] directions = Direction.values();
        int current = currentDirection.ordinal();
        if(turnRight) {
            currentDirection = directions[(current + 1) % directions.length];
        } else {
            currentDirection = directions[(current + 3) % directions.length];
        }
    }

    public boolean isInWorld(Position position) {
        return position.getX() >= 0 && position.getX() <= width || position.getY() >= 0 && position.getY() <= height;
    }
}
