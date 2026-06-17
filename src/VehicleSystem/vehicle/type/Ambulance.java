package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.EmergencyBehavior;

public class Ambulance extends Vehicle {
    public Ambulance(double startX, double startY, int i, int i1, double v, EmergencyBehavior emergencyBehavior) {
        // Rộng 25, Dài 60
        super(startX, startY, 17.0, 45.0, 1.2, new EmergencyBehavior());
    }
}
