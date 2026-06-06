package vehicle.behavior;

import vehicle.Vehicle;
import java.util.List;

public class EmergencyDriver implements DrivingStrategy {
    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {
        // Xe ưu tiên (Cứu thương/Cứu hỏa) bỏ qua hoàn toàn đèn đỏ

        // Kiểm tra khoảng cách trực diện để tránh đâm đuôi xe trước
        if (frontVehicle != null) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());
            if (distanceToFront < 20.0) {
                // Giảm tốc tạm thời bằng xe trước để chờ xe đó dạt ra nhường đường
                current.setSpeed(frontVehicle.getSpeed());
                return;
            }
        }

        // Luôn duy trì vận tốc cao nhất
        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.5);
        }
    }
}
