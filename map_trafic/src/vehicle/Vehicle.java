package vehicle;

import math.Vector2D;

public interface Vehicle {
    void move();
    void stop();
    Vector2D getPosition();

    // Các hàm phục vụ cho AI DriverBehavior
    double getX();
    double getY();
    double getSpeed();
    void setSpeed(double speed);
    double getMaxSpeed();
    double getWidth();
    double getOriginalY();
    void setTargetY(double targetY);
    boolean isShiftingLane();
    void setShiftingLane(boolean shifting);
    boolean isEmergency();
}