package VehicleSystem.vehicle.type;

import VehicleSystem.behavior.DrivingStrategy;
import VehicleSystem.vehicle.Vehicle;

public class Car extends Vehicle {

    // 🚨 Đã sửa chữ NormalBehavior thành DrivingStrategy ở đây
    public Car(double startX, double startY, double width, double length, double baseMaxSpeed, DrivingStrategy driver) {
        // 🚨 Chỉ truyền đúng chữ "driver" vào super, KHÔNG có chữ "new" gì ở đây cả
        super(startX, startY, width, length, baseMaxSpeed, driver);
    }
}