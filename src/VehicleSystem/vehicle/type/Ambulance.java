package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.EmergencyBehavior;

public class Ambulance extends Vehicle {
    public Ambulance(double startX, double startY) {
        // Rộng 25, Dài 60
        super(startX, startY, 25.0, 60.0, 7.0, new EmergencyBehavior());
    }
}
