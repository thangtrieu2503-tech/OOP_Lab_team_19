
package DriverBehavior;

import vehicle.Vehicle;
import java.util.List;

public class NormalDriver implements DrivingStrategy {
    private final double SAFE_DISTANCE = 40.0;

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {
        boolean emergencyBehind = false;
        for (Vehicle other : allVehicles) {
            if (other.isEmergency() && other.getY() == current.getOriginalY() &&
                    current.getX() > other.getX() && (current.getX() - other.getX()) < 120) {
                emergencyBehind = true;
                break;
            }
        }

        if (emergencyBehind) {
            current.setTargetY(current.getOriginalY() + 25);
            current.setSpeed(current.getMaxSpeed() * 0.6);
            return;
        } else if (!current.isShiftingLane()) {
            current.setTargetY(current.getOriginalY());
        }

        if (frontVehicle != null) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());
            if (distanceToFront < SAFE_DISTANCE) {
                current.setSpeed(Math.max(0, frontVehicle.getSpeed() - 0.5));
                return;
            }
        }

        if (isRedLight && distanceToLight > 0 && distanceToLight < 80) {
            double brakeFactor = distanceToLight / 80.0;
            current.setSpeed(current.getMaxSpeed() * brakeFactor);
            return;
        }

        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.2);
        }
    }
}