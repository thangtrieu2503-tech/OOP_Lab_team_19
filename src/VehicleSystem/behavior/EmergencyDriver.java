package VehicleSystem.behavior; // (Hoặc package VehicleSystem.vehicle tùy cấu trúc hiện tại của ông)

import VehicleSystem.vehicle.Vehicle;

import java.util.List;

public class EmergencyDriver implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles, String upcomingLightColor) {
        // Khoảng cách an toàn chỉ 40px (Dám dí sát đít xe trước hơn so với NormalDriver là 60px)
        double safeDistance = 40.0;
        boolean obstacleAhead = false;

        // ==========================================
        // 1. TÍNH NĂNG CHỐNG XUYÊN THẤU (Collision Avoidance)
        // ==========================================
        for (Vehicle other : allVehicles) {
            if (other == me) continue;

            double dist = me.distanceTo(other);
            if (dist < safeDistance) {
                obstacleAhead = true;
                break;
            }
        }

        // ==========================================
        // 2. KHÔNG THÈM QUAN TÂM ĐÈN GIAO THÔNG (Vượt đèn đỏ)
        // ==========================================
        // (Bỏ trống hoàn toàn phần check upcomingLightColor ở đây)
        // Cứu thương cứ thấy đường trống là đạp ga tới bến!

        // ==========================================
        // 3. QUYẾT ĐỊNH HÀNH ĐỘNG
        // ==========================================
        if (obstacleAhead) {
            // Dù là xe ưu tiên nhưng nếu phía trước kẹt cứng thì vẫn phải phanh hờ chờ ngta dạt ra
            me.brake();
        } else {
            // Đạp ga tới bến với tốc độ gốc (Tốc độ này đã được x1.5 ở bên file Ambulance rồi)
            me.moveForward(me.getBaseSpeed());
        }
    }
}