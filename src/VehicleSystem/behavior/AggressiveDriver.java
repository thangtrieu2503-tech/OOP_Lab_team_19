package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.map.Intersection;
import MapSystem.light.LightState;
import java.util.List;

public class AggressiveDriver implements DrivingStrategy {

    // Hệ số bạo chúa: Chạy lố 30% công suất, đạp ga thốc gấp rưỡi
    private final double SPEED_MULTIPLIER = 1.3;
    private final double ACCEL_MULTIPLIER = 1.5;

    // Mắt mù: Đến cực gần mới nhìn đèn và phanh sát vạch
    private final double SENSOR_RANGE = 70.0;   // Ngắn hơn mức 120 của người thường
    private final double STOP_LINE_DIST = 15.0; // Đỗ sát sạt vạch

    @Override
    public void drive(Vehicle vehicle, List<Vehicle> allVehicles) {
        boolean obstacleAhead = false;
        boolean redLightAhead = false;

        // 1. Lọc va chạm (Chưa code) - Sẽ để khoảng cách an toàn cực ngắn

        // 2. Mắt thần nhìn đèn (Logic Vượt đèn Vàng)
        Intersection targetNode = vehicle.getTargetNode();

        if (targetNode != null && targetNode.getTrafficLight() != null) {
            double distanceToNode = vehicle.getPosition().distanceTo(targetNode.getPosition());
            LightState currentState = targetNode.getTrafficLight().getState();

            // ĐIỂM KHÁC BIỆT: Gã này KHÔNG check LightState.YELLOW. Thấy vàng là nó đạp ga qua luôn!
            if (distanceToNode < SENSOR_RANGE && currentState == LightState.RED) {
                if (distanceToNode > STOP_LINE_DIST) {
                    redLightAhead = true;
                }
            }
        }

        // 3. Ra quyết định hành động
        double actualAccel = 0.1 * ACCEL_MULTIPLIER;
        double actualLimit = vehicle.getBaseMaxSpeed() * SPEED_MULTIPLIER;

        if (obstacleAhead || redLightAhead) {
            // Đã phanh là phanh cháy lốp (Gia tốc âm lớn)
            vehicle.setAcceleration(-0.5);
        } else {
            vehicle.setAcceleration(actualAccel);
        }

        vehicle.setMaxSpeed(actualLimit);
    }
}