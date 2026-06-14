package DriverBehavior;

import vehicle.Vehicle;
import java.util.List;

public class EmergencyDriver implements DrivingStrategy {
    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {
        if (frontVehicle != null) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());
            if (distanceToFront < 20.0) {
                current.setSpeed(frontVehicle.getSpeed());
                return;
            }
        }

        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.5);
        }
    }
}