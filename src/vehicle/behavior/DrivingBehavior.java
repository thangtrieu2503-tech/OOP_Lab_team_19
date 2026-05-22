package vehicle.behavior;

import vehicle.Vehicle;

public interface DrivingBehavior {
    void handleMovement(Vehicle currentVehicle, Vehicle vehicleInFront, boolean isRedLight);
}
