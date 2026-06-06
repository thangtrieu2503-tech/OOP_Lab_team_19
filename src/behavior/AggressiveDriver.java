package behavior;

import vehicle.Vehicle;
import java.util.List;

public class AggressiveDriver implements DrivingStrategy {
    private final double SAFE_DISTANCE = 25.0; // Bám đuôi sát sạt

    @Override
    public void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles) {

        // 1. LOGIC VƯỢT XE TỰ DO
        if (frontVehicle != null) {
            // Tính khoảng cách hình học 2D giữa 2 xe
            double dx = frontVehicle.getX() - current.getX();
            double dy = frontVehicle.getY() - current.getY();
            double distanceToFront = Math.sqrt(dx * dx + dy * dy) - current.getWidth();

            if (distanceToFront < 60.0 && frontVehicle.getSpeed() < current.getMaxSpeed()) {
                boolean targetLaneBlocked = false;

                // Thuật toán quét radar 2D xem xung quanh có xe nào đang chắn hông không
                for (Vehicle other : allVehicles) {
                    if (other != current) {
                        double odx = other.getX() - current.getX();
                        double ody = other.getY() - current.getY();
                        double distToOther = Math.sqrt(odx * odx + ody * ody);

                        // Nếu có xe ở quá gần trong bán kính 50px
                        if (distToOther < 50.0) {
                            targetLaneBlocked = true;
                            break;
                        }
                    }
                }

                // Nếu hông trống -> Bứt tốc vượt!
                if (!targetLaneBlocked) {
                    current.setShiftingLane(true);
                    current.setSpeed(current.getMaxSpeed() * 1.2); // Tăng tốc 20% để vượt

                    // Ảo thuật: Lách nhẹ tọa độ sang bên hông xe trước để vượt qua
                    // (Tự động lệch góc vuông với hướng đi hiện tại để lách)
                    current.setSpeed(current.getMaxSpeed() * 1.2);
                    return;
                }
            }
        }

        // 2. LOGIC HOÀN LÀN: Đường trống thì tắt trạng thái vượt
        if (current.isShiftingLane() && frontVehicle == null) {
            current.setShiftingLane(false);
        }

        // 3. LOGIC BÁM ĐUÔI SÁT (Nếu không vượt được)
        if (frontVehicle != null && !current.isShiftingLane()) {
            double dx = frontVehicle.getX() - current.getX();
            double dy = frontVehicle.getY() - current.getY();
            double distanceToFront = Math.sqrt(dx * dx + dy * dy) - current.getWidth();

            if (distanceToFront < SAFE_DISTANCE) {
                current.setSpeed(frontVehicle.getSpeed()); // Đi bằng tốc độ xe trước
                return;
            }
        }

        // 4. PHANH GẤP ĐÈN ĐỎ (Khoảng cách 2D dưới 40px mới chịu phanh)
        if (isRedLight && distanceToLight > 0 && distanceToLight < 40) {
            current.setSpeed(0);
            return;
        }

        // 5. LUÔN MUỐN PHÓNG NHANH
        if (current.getSpeed() < current.getMaxSpeed()) {
            current.setSpeed(current.getSpeed() + 0.4);
        }
    }
}