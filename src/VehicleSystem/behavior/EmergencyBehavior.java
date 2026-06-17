package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import java.util.List;

public class EmergencyBehavior implements DrivingStrategy {

    // Hệ số ưu tiên: Chạy lố 50% công suất, đạp ga mạnh
    private final double SPEED_MULTIPLIER = 1.2;
    private final double ACCEL_MULTIPLIER = 1.2;

    @Override
    public void drive(Vehicle vehicle, List<Vehicle> allVehicles) {
        // Biến redLightAhead đã bị xóa sổ hoàn toàn khỏi bộ não này!
        boolean obstacleAhead = false;

        // 1. Chỉ check xem có xe cản đường không để khỏi gây tai nạn
        // (Do anh em mình tạm bỏ tính năng dạt ra nhường đường,
        // nên nó vẫn phải đạp phanh chờ thằng đằng trước đi khuất)

        // 2. KHÔNG CÓ LOGIC CHECK ĐÈN GIAO THÔNG Ở ĐÂY.
        // Tới ngã tư đèn đỏ nó vẫn phi thẳng!

        // 3. Ra quyết định
        double actualAccel = 0.1 * ACCEL_MULTIPLIER;
        double actualLimit = vehicle.getBaseMaxSpeed() * SPEED_MULTIPLIER;

        if (obstacleAhead) {
            vehicle.setAcceleration(-0.6); // Phanh cực gắt
        } else {
            vehicle.setAcceleration(actualAccel);
        }

        vehicle.setMaxSpeed(actualLimit);
    }
}