package DriverBehavior;

import VehicleSystem.Vehicle;
import java.util.List;

public class AggressiveDriver implements DrivingStrategy {
    private final double SAFE_DISTANCE = 20.0; // Bám đuôi rất sát

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {

        // 1. Logic Vượt xe: Nếu có xe trước chắn đường và xe đó chạy chậm hơn tốc độ mong muốn của mình
        if (frontVehicle != null) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());

            if (distanceToFront < 50.0 && frontVehicle.getSpeed() < current.getMaxSpeed()) {
                // Kiểm tra xem làn bên trái (lệch lên trên 30px) có trống không để thực hiện vượt
                boolean targetLaneBlocked = false;
                double overtakeY = current.getOriginalY() - 30;

                for (Vehicle other : allVehicles) {
                    if (Math.abs(other.getY() - overtakeY) < 15 && Math.abs(other.getX() - current.getX()) < 80) {
                        targetLaneBlocked = true; // Làn bên cạnh đang vướng xe khác, không vượt được
                        break;
                    }
                }

                if (!targetLaneBlocked) {
                    current.setTargetY(overtakeY);
                    current.setShiftingLane(true);
                    current.setSpeed(current.getMaxSpeed() * 1.2); // Tăng tốc 20% đánh lái vượt lên bứt phá
                    return;
                }
            }
        }

        // Nếu đã vượt qua hẳn xe trước (đường trước mặt trống), tự động xi-nhan chuyển về làn cũ
        if (current.isShiftingLane() && frontVehicle == null) {
            current.setTargetY(current.getOriginalY());
            current.setShiftingLane(false);
        }

        // 2. Logic Giữ khoảng cách tối thiểu nếu không thể vượt
        if (frontVehicle != null && !current.isShiftingLane()) {
            double distanceToFront = frontVehicle.getX() - (current.getX() + current.getWidth());
            if (distanceToFront < SAFE_DISTANCE) {
                current.setSpeed(frontVehicle.getSpeed());
                return;
            }
        }

        // 3. Logic Phanh gấp khi gặp đèn đỏ (Đến rất gần vạch mới phanh đột ngột)
        if (isRedLight && distanceToLight > 0 && distanceToLight < 40) {
            current.setSpeed(0);
            return;
        }

        // 4. Luôn có xu hướng tăng tốc nhanh
        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.4);
        }
    }
}