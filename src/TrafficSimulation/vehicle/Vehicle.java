package vehicle;

import math.Vector2D;

public interface Vehicle {

    void move();

    void stop();

    double getSpeed();

    Vector2D getPosition();
}