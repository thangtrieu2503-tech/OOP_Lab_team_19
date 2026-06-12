package VehicleSystem.behavior;
import VehicleSystem.vehicle.Vehicle;

import java.util.List;

public class NormalDriver implements DrivingStrategy {
    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles, String upcomingLightColor) {

        // (Radar quét xe đằng trước ông cứ để nguyên như cũ nhé)

        boolean stopForLight = false;
        if (upcomingLightColor.equals("RED") || upcomingLightColor.equals("YELLOW")) {
            // NẾU CÁCH NGÃ TƯ DƯỚI 40PX VÀ **CHƯA VÀO TRONG BÙNG BINH** THÌ MỚI PHANH
            if (me.distanceToNextNode() < 40.0 && !me.isTurning()) {
                stopForLight = true;
            }
        }

        if (stopForLight) {  // Nếu ông ghép radar va chạm vào thì là: if (obstacleAhead || stopForLight)
            me.brake();
        } else {
            me.moveForward(me.getBaseSpeed());
        }
    }
}