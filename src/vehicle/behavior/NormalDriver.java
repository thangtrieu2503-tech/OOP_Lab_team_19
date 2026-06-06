package vehicle.behavior;

import vehicle.Vehicle;
import java.util.List;

public class NormalDriver implements DrivingStrategy {
    private final double SAFE_DISTANCE = 40.0; // Khoảng cách an toàn (pixel)

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {
        // 1. Logic Nhường xe ưu tiên: Kiểm tra xem có xe cứu thương/cứu hỏa nào đang bám đuôi phía sau không
        boolean emergencyBehind = false;
        for (Vehicle other : allVehicles) {
            if (other.isEmergency() && other.getY() == current.getOriginalY() &&
                    current.getX() > other.getX() && (current.getX() - other.getX()) < 120) {
                emergencyBehind = true;
                break;
            }
        }

        if (emergencyBehind) {
            current.setTargetY(current.getOriginalY() + 25); // Chủ động dạt sang lề phải 25px để nhường đường
            current.setSpeed(current.getMaxSpeed() * 0.6);   // Giảm tốc cho xe ưu tiên dễ dàng vượt qua
            return;
        } else if (!current.isShiftingLane()) {
            current.setTargetY(current.getOriginalY()); // Nếu đường trống, tự động quay về làn chính cũ
        }

        // 2. Logic Giữ khoảng cách với xe trước
        if (frontVehicle != null) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());
            if (distanceToFront < SAFE_DISTANCE) {
                current.setSpeed(Math.max(0, frontVehicle.getSpeed() - 0.5));
                return;
            }
        }

        // 3. Logic Dừng đèn đỏ
        if (isRedLight && distanceToLight > 0 && distanceToLight < 80) {
            double brakeFactor = distanceToLight / 80.0; // Giảm tốc mượt mà dựa trên khoảng cách tới vạch
            current.setSpeed(current.getMaxSpeed() * brakeFactor);
            return;
        }

        // 4. Trạng thái bình thường: Di chuyển ổn định đạt tốc độ tối đa
        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.2);
        }
    }
}
