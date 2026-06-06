package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public class Bicycle extends Vehicle {
    public Bicycle(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        super(waypoints, maxSpeed, strategy);
        this.width = 22;
        this.height = 10;
    }
}