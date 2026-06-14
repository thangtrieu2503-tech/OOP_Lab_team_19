package DriverBehavior;

import vehicle.Vehicle;
import java.util.List;

public interface DrivingStrategy {
    void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles);
}