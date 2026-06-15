package VehicleSystem.behavior; // Hoặc traffic.vehicle tùy cấu trúc hiện tại của ông

import VehicleSystem.vehicle.Vehicle;
import java.util.List;

public class NormalDriver implements DrivingStrategy {
    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles, String upcomingLightColor) {

        // CẮT BỎ HOÀN TOÀN TÍNH NĂNG NHÌN ĐÈN ĐỎ VÀ NHÌN XE KHÁC
        // Ép xe luôn luôn tiến về phía trước để test AI bẻ lái!

        me.moveForward(me.getBaseSpeed());
    }
}