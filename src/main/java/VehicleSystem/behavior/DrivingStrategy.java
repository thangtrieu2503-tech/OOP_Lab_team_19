package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import java.util.List;

public interface DrivingStrategy {
    void drive(Vehicle vehicle, List<Vehicle> allVehicles);
}