package DriverBehavior;

import vehicle.Vehicle;
import java.util.List;

public class AggressiveDriver implements DrivingStrategy {
    private final double SAFE_DISTANCE = 20.0;

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {
        if (frontVehicle != null) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());

            if (distanceToFront < 50.0 && frontVehicle.getSpeed() < current.getMaxSpeed()) {
                boolean targetLaneBlocked = false;
                double overtakeY = current.getOriginalY() - 30;

                for (Vehicle other : allVehicles) {
                    if (Math.abs(other.getY() - overtakeY) < 15 && Math.abs(other.getX() - current.getX()) < 80) {
                        targetLaneBlocked = true;
                        break;
                    }
                }

                if (!targetLaneBlocked) {
                    current.setTargetY(overtakeY);
                    current.setShiftingLane(true);
                    current.setSpeed(current.getMaxSpeed() * 1.2);
                    return;
                }
            }
        }

        if (current.isShiftingLane() && frontVehicle == null) {
            current.setTargetY(current.getOriginalY());
            current.setShiftingLane(false);
        }

        if (frontVehicle != null && !current.isShiftingLane()) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());
            if (distanceToFront < SAFE_DISTANCE) {
                current.setSpeed(frontVehicle.getSpeed());
                return;
            }
        }

        if (isRedLight && distanceToLight > 0 && distanceToLight < 40) {
            current.setSpeed(0);
            return;
        }

        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.4);
        }
    }
}