package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.NormalBehavior;

public class Bus extends Vehicle {
    public Bus(double startX, double startY, int i, int i1, double v, NormalBehavior normalBehavior) {
        // Xác xe to dài: rộng 30, dài 90, tốc độ rùa bò 3.5
        super(startX, startY, 30.0, 90.0, 2.0, new NormalBehavior());
    }
}