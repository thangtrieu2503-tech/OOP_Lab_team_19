package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.NormalBehavior;

public class Car extends Vehicle {
    public Car(double startX, double startY, int i, int i1, double v, NormalBehavior normalBehavior) {
        // Rộng 20, Dài 40
        super(startX, startY, 16.0, 36.0, 1.0, new NormalBehavior());
    }
}