package behavior;

import vehicle.Vehicle;
import java.util.List;

public class NormalDriver implements DrivingStrategy {
    private final double SAFE_DISTANCE = 45.0;

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {

        // 1. Giữ khoảng cách 2D an toàn với xe trước
        if (frontVehicle != null) {
            double dx = frontVehicle.getX() - current.getX();
            double dy = frontVehicle.getY() - current.getY();
            double distanceToFront = Math.sqrt(dx * dx + dy * dy) - current.getWidth();

            if (distanceToFront < SAFE_DISTANCE) {
                current.setSpeed(frontVehicle.getSpeed());
                return;
            }
        }

        // 2. Gặp đèn đỏ thì hãm phanh từ xa (dưới 100px)
        if (isRedLight && distanceToLight > 0 && distanceToLight < 100) {
            if (distanceToLight < 20) {
                current.setSpeed(0); // Dừng hẳn trước đèn
            } else {
                current.setSpeed(current.getSpeed() - 0.25); // Hãm phanh từ từ
            }
            return;
        }

        // 3. Tăng tốc thong thả
        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.15);
        }
    }
}