package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;
import MapSystem.light.TrafficLight;
import java.util.List;

public class NormalBehavior implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        boolean isRedLightAhead = false;
        double targetAcceleration = 0.04;
        double targetMaxSpeed = me.getBaseMaxSpeed();

        if (me.getTargetNode() != null && me.getTargetNode().getTrafficController() != null) {
            TrafficController controller = me.getTargetNode().getTrafficController();

            if (!controller.getLights().isEmpty()) {

                int lightIndex = (Math.abs(me.getY() - me.getTargetNode().getPosition().getY()) >
                        Math.abs(me.getX() - me.getTargetNode().getPosition().getX())) ? 1 : 0;

                if (lightIndex >= controller.getLights().size()) {
                    lightIndex = 0;
                }

                LightState currentState = controller.getLights().get(lightIndex).getCurrentState();

                // 🚀 1. Ý TƯỞNG CỦA ÔNG: TẠO KHU VỰC NGÃ TƯ (Ô VUÔNG XANH CỐ ĐỊNH)
                // Kích thước từ tâm ngã tư ra đến mép vạch người đi bộ (Nửa cạnh hình vuông)
                // Ông có thể tăng giảm số 90.0 này cho khớp đúng với mép ô xanh ông vẽ
                double boxSize = 90.0;

                double carX = me.getX();
                double carY = me.getY();
                double targetX = me.getTargetNode().getPosition().getX();
                double targetY = me.getTargetNode().getPosition().getY();

                // Kiểm tra xem xe có đang nằm trong Ô Vuông của ngã tư đích không?
                boolean inTargetBox = Math.abs(carX - targetX) < boxSize && Math.abs(carY - targetY) < boxSize;

                // Kiểm tra xem xe có đang nằm trong Ô Vuông của ngã tư VỪA ĐI QUA không?
                boolean inPrevBox = false;
                if (me.getPreviousNode() != null) {
                    double prevX = me.getPreviousNode().getPosition().getX();
                    double prevY = me.getPreviousNode().getPosition().getY();
                    inPrevBox = Math.abs(carX - prevX) < boxSize && Math.abs(carY - prevY) < boxSize;
                }

                // Nếu xe nằm trong BẤT KỲ ô vuông ngã tư nào -> Mặc định đi, tắt Rada!
                boolean isInsideIntersection = inTargetBox || inPrevBox;

                // 🚀 2. Ý TƯỞNG CỦA ÔNG: RADA CHỈ QUÉT TRƯỚC MẶT
                // Tính toán Vector hướng từ xe tới ngã tư
                double dxToTarget = targetX - carX;
                double dyToTarget = targetY - carY;

                // Lấy hướng mũi xe hiện tại (Dùng lượng giác)
                double carDirX = Math.cos(Math.toRadians(me.getAngle()));
                double carDirY = Math.sin(Math.toRadians(me.getAngle()));

                // Tích vô hướng (Dot Product): > 0 nghĩa là ngã tư đang ở trước mặt kính lái.
                boolean isTargetInFront = (dxToTarget * carDirX + dyToTarget * carDirY) > 0;

                Vehicle.TurnDirection myTurn = me.getTurnDirection();

                // 🚀 3. KÍCH HOẠT PHANH VỚI BỘ LỌC HOÀN HẢO
                // NẾU: Không nằm trong ô vuông + Ngã tư ở trước mặt + Đèn Đỏ + Không rẽ phải
                if (!isInsideIntersection && isTargetInFront
                        && (currentState == LightState.RED || currentState == LightState.YELLOW)
                        && myTurn != Vehicle.TurnDirection.RIGHT) {

                    double distToNode = me.getPosition().distanceTo(me.getTargetNode().getPosition());
                    double stopLine = boxSize + (me.getLength() / 2.0);

                    // Rada giờ chỉ quét đúng 60px trước vạch dừng
                    if (distToNode <= stopLine + 60.0) {
                        isRedLightAhead = true;

                        if (distToNode <= stopLine + 5.0) {
                            targetAcceleration = -2.0;
                            targetMaxSpeed = 0;
                            if (me.getSpeed() < 0.5) {
                                me.setMaxSpeed(0);
                                me.setAcceleration(0);
                            }
                        } else {
                            targetAcceleration = -0.2; // Rà phanh
                            targetMaxSpeed = me.getBaseMaxSpeed() * 0.3;
                        }
                    }
                }
            }
        }

        if (isRedLightAhead) {
            me.setAcceleration(targetAcceleration);
            me.setMaxSpeed(targetMaxSpeed);
            return;
        }

        me.setMaxSpeed(targetMaxSpeed);
        me.setAcceleration(targetAcceleration);
    }
}