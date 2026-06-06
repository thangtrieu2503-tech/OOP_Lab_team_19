package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public class Car extends Vehicle {
    public Car(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        super(waypoints, maxSpeed, strategy);
        this.width = 45;
        this.height = 22;
    }
}