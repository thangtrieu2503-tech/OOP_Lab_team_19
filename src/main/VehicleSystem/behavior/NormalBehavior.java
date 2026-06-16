package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
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

        // Thay vì getTrafficLight(), ta phải gọi getTrafficController()
        if (targetNode != null && targetNode.getTrafficController() != null) {

            // Lấy danh sách các đèn ở ngã tư này.
            // Tạm thời anh em mình lấy đại cái đèn đầu tiên (index 0) để check trước
            var lights = targetNode.getTrafficController().getLights();

            if (!lights.isEmpty()) {
                // Dùng công cụ Pytago đo khoảng cách
                double distanceToNode = vehicle.getPosition().distanceTo(targetNode.getPosition());

                // Trích xuất trạng thái màu của cái đèn đó
                LightState currentState = lights.get(0).getCurrentState();

                // ... (Logic đạp phanh hay vượt đèn ông giữ nguyên ở dưới nhé)
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