package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;
import java.util.List;

public class EmergencyBehavior implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        me.setMaxSpeed(me.getBaseMaxSpeed() * 1.2);
        double targetAcceleration = 0.06;

        // 1. CHECK ĐÈN (Ưu tiên đi qua nhưng giảm tốc độ an toàn)
        if (me.getTargetNode() != null && me.getTargetNode().getTrafficController() != null) {
            TrafficController controller = me.getTargetNode().getTrafficController();
            if (!controller.getLights().isEmpty()) {
                double dx = Math.abs(me.getTargetNode().getPosition().getX() - me.getPosition().getX());
                double dy = Math.abs(me.getTargetNode().getPosition().getY() - me.getPosition().getY());
                int lightIndex = (dx > dy) ? 0 : 1;
                if (lightIndex >= controller.getLights().size()) lightIndex = 0;

                if (controller.getLights().get(lightIndex).getCurrentState() == LightState.RED) {
                    targetAcceleration = 0.01; // Đi chậm qua ngã tư
                }
            }
        }

        // 2. RADAR & CHỐNG VA CHẠM
        // ... (Giữ nguyên logic dẹp đường và chuyển làn) ...

        me.setAcceleration(targetAcceleration);
    }
}