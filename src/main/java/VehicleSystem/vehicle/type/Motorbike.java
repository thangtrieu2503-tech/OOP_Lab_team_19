package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.NormalBehavior;

public class Motorbike extends Vehicle {
    public Motorbike(double startX, double startY, int i, int i1, double v, NormalBehavior normalBehavior) {
        // Nhỏ gọn: rộng 12, dài 25, phóng nhanh 6.0, não trẻ trâu bốc lửa
        super(startX, startY, 7.0, 18.0, 0.7, new NormalBehavior());
    }
}