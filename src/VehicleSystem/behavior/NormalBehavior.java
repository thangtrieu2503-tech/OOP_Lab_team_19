package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.map.Intersection;
import MapSystem.light.LightState;
import java.util.List;

public class NormalBehavior implements DrivingStrategy {

    private final double SPEED_MULTIPLIER = 1.0;
    private final double ACCEL_MULTIPLIER = 1.0;

    // Thêm 2 thông số cho Radar
    private final double SENSOR_RANGE = 120.0; // Khoảng cách bắt đầu nhìn thấy đèn (Pixel)
    private final double STOP_LINE_DIST = 30.0; // Khoảng cách cách tâm ngã tư phải dừng lại (Vạch kẻ đường)

    @Override
    public void drive(Vehicle vehicle, List<Vehicle> allVehicles) {
        boolean obstacleAhead = false;
        boolean redLightAhead = false;

        // 1. Logic Check Xe Phía Trước (Như cũ)
        // ... (Check va chạm)

        // 2. Logic Check Đèn Đỏ
        Intersection targetNode = vehicle.getTargetNode(); // Lấy ngã tư đang đi tới

        if (targetNode != null && targetNode.getTrafficLight() != null) {
            // Dùng công cụ Pytago đo khoảng cách
            double distanceToNode = vehicle.getPosition().distanceTo(targetNode.getPosition());
            LightState currentState = targetNode.getTrafficLight().getState();

            // Nếu xe đã vào Tầm Nhìn VÀ đèn đang Đỏ/Vàng
            if (distanceToNode < SENSOR_RANGE && (currentState == LightState.RED || currentState == LightState.YELLOW)) {

                // Kích hoạt luật "Qua vạch rồi thì đi luôn":
                // Chỉ phanh nếu xe vẫn còn cách tâm ngã tư xa hơn vạch dừng
                if (distanceToNode > STOP_LINE_DIST) {
                    redLightAhead = true;
                }
            }
        }

        // 3. Ra quyết định Cuối cùng
        double actualAccel = 0.1 * ACCEL_MULTIPLIER;
        double actualLimit = vehicle.getBaseMaxSpeed() * SPEED_MULTIPLIER;

        // Ưu tiên phanh: Có xe cản đường HOẶC Có đèn đỏ thì đều đạp phanh
        if (obstacleAhead || redLightAhead) {
            vehicle.setAcceleration(-0.3); // Đạp phanh (Gia tốc âm)
        } else {
            vehicle.setAcceleration(actualAccel); // Bơm ga
        }

        vehicle.setMaxSpeed(actualLimit);
    }
}