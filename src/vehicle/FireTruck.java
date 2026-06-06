package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public class FireTruck extends Vehicle {
    public FireTruck(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        super(waypoints, maxSpeed, strategy);
        this.width = 85;
        this.height = 35;
    }
}