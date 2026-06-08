package vehicle;

import math.Vector2D;

public class Car implements Vehicle {

    private double speed;
    private Vector2D position;

    public Car() {
        speed = 5;
        position = new Vector2D(0, 0);
    }

    @Override
    public void move() {
        position.x += speed;
        System.out.println("Car moving: " + position.x);
    }

    @Override
    public void stop() {
        speed = 0;
    }

    @Override
    public double getSpeed() {
        return speed;
    }

    @Override
    public Vector2D getPosition() {
        return position;
    }
}