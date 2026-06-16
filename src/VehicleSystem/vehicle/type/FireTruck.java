package VehicleSystem.vehicle.type;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.behavior.EmergencyBehavior;

public class FireTruck extends Vehicle {
    public FireTruck(double startX, double startY, int i, int i1, double v, EmergencyBehavior emergencyBehavior) {
        // Xe cứu hỏa bự nhất: rộng 32 (gần choán hết làn), dài 85, não ưu tiên khẩn cấp
        super(startX, startY, 32.0, 85.0, 2.5, new EmergencyBehavior());
    }
}