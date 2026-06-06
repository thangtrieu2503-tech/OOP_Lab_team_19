package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public class Bus extends Vehicle {
    public Bus(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        super(waypoints, maxSpeed, strategy);
        this.width = 80;
        this.height = 32;
    }
}
