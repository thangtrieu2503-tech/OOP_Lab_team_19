package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;

import java.util.List;

public class AggressiveDriver implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles, String upcomingLightColor) {
        // 1. CHUYÊN GIA DÍ ĐÍT: Khoảng cách an toàn cực ngắn (chỉ 25px)
        double safeDistance = 25.0;
        boolean obstacleAhead = false;

        for (Vehicle other : allVehicles) {
            if (other == me) continue;

            if (me.distanceTo(other) < safeDistance) {
                obstacleAhead = true;
                break;
            }
        }

        // 2. VƯỢT ĐÈN VÀNG: Mắt điếc tai ngơ với đèn Vàng, chỉ dừng khi Đỏ chót
        boolean stopForLight = false;
        // Chỉ check "RED", bỏ qua "YELLOW"
        if (upcomingLightColor.equals("RED")) {
            // Phanh cực gấp, sát vạch (20px) mới thèm đạp phanh
            if (me.distanceToNextNode() < 20.0) {
                stopForLight = true;
            }
        }

        // 3. QUYẾT ĐỊNH HÀNH ĐỘNG
        if (obstacleAhead || stopForLight) {
            me.brake(); // Két!! Phanh cháy lốp
        } else {
            // Đạp ga lút cán: Tốc độ luôn nhân 1.5 lần
            me.moveForward(me.getBaseSpeed() * 1.5);
        }
    }
}