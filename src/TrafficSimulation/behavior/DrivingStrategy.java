package TrafficSimulation.behavior;

import TrafficSimulation.vehicle.Vehicle;
import java.util.List;

public interface DrivingStrategy {
    void drive(Vehicle me, List<Vehicle> allVehicles, String upcomingLightColor);
}