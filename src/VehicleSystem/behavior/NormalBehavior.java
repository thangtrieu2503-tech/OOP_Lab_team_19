package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;
import java.util.List;

public class NormalBehavior implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        boolean isRedLightAhead = false;
        double targetAcceleration = 0.04;
        double targetMaxSpeed = me.getBaseMaxSpeed();

        // 1. CHECK ĐÈN TÍN HIỆU (NHÌN ĐÈN TRƯỚC MẶT)
        if (me.getTargetNode() != null && me.getTargetNode().getTrafficController() != null) {
            TrafficController controller = me.getTargetNode().getTrafficController();
            if (!controller.getLights().isEmpty()) {
                double dx = Math.abs(me.getTargetNode().getPosition().getX() - me.getPosition().getX());
                double dy = Math.abs(me.getTargetNode().getPosition().getY() - me.getPosition().getY());
                int lightIndex = (dx > dy) ? 0 : 1;
                if (lightIndex >= controller.getLights().size()) lightIndex = 0;

                LightState currentState = controller.getLights().get(lightIndex).getCurrentState();
                double distToLight = me.getPosition().distanceTo(me.getTargetNode().getPosition());

                // Vạch dừng chuẩn = vị trí đèn + nửa chiều dài xe
                double stopLineDistance = 85.0 + (me.getLength() / 2.0);

                if (currentState == LightState.RED || currentState == LightState.YELLOW) {
                    if (distToLight < stopLineDistance + 80.0) {
                        isRedLightAhead = true;
                        // Phanh gắt nếu sát vạch
                        if (distToLight <= stopLineDistance + 5.0) {
                            targetAcceleration = -1.0;
                            targetMaxSpeed = 0;
                        } else {
                            targetAcceleration = -0.2;
                        }
                    }
                }
            }
        }

        // Nếu đèn đỏ thì dừng hẳn và không chạy logic khác
        if (isRedLightAhead) {
            me.setAcceleration(targetAcceleration);
            me.setMaxSpeed(targetMaxSpeed);
            return;
        }

        // 2. RADAR QUÉT VẬT CẢN & NHƯỜNG ĐƯỜNG (Chỉ chạy khi đèn Xanh)
        // ... (Giữ nguyên logic tránh xe và nhường đường của ông ở đây) ...

        me.setMaxSpeed(targetMaxSpeed);
        me.setAcceleration(targetAcceleration);
    }
}