package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public class Ambulance extends Vehicle {
    public Ambulance(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        super(waypoints, maxSpeed, strategy);
        this.width = 55;
        this.height = 25;
    }
}