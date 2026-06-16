package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.NormalBehavior;

public class Car extends Vehicle {
    public Car(double startX, double startY) {
        // Rộng 20, Dài 40
        super(startX, startY, 20.0, 40.0, 5.0, new NormalBehavior());
    }
}