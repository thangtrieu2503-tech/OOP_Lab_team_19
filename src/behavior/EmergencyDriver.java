package behavior;

import vehicle.Vehicle;
import java.util.List;

public class EmergencyDriver implements DrivingStrategy {

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {

        // 1. Né xe cản đường bằng radar 2D
        if (frontVehicle != null) {
            double dx = frontVehicle.getX() - current.getX();
            double dy = frontVehicle.getY() - current.getY();
            double distanceToFront = Math.sqrt(dx * dx + dy * dy) - current.getWidth();

            if (distanceToFront < 50.0) {
                // Bật trạng thái lách làn để lách qua
                current.setShiftingLane(true);
                return;
            }
        }

        if (current.isShiftingLane() && frontVehicle == null) {
            current.setShiftingLane(false);
        }

        // 2. Chấp mọi loại đèn đỏ (Không viết code xử lý đèn đỏ ở đây)

        // 3. Đạp ga lút số để đi cứu người
        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.5);
        }
    }
}