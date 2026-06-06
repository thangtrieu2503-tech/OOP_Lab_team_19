package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public class Motorbike extends Vehicle {
    public Motorbike(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        super(waypoints, maxSpeed, strategy);
        this.width = 28;
        this.height = 12;
    }
}
