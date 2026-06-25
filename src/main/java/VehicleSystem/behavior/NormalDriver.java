package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;

import java.util.List;

public class NormalDriver implements DrivingStrategy {

    private void handleYielding(Vehicle me) {
        if (!me.isRequestedToYield()) return;
        int myLane = me.getCurrentLane();
        if (myLane > 0) me.changeLane(myLane - 1);
        else if (myLane < 2) me.changeLane(myLane + 1);
        me.setRequestedToYield(false);
    }

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        handleYielding(me);

        double targetAcceleration = 0.04;
        double targetMaxSpeed = me.getBaseMaxSpeed();
        double carDirX = Math.cos(Math.toRadians(me.getAngle()));
        double carDirY = Math.sin(Math.toRadians(me.getAngle()));

        boolean isInsideIntersection = false;
        double distToNode = 0.0;
        double boxSize = 25.0;

        if (me.getTargetNode() != null) {
            distToNode = me.getPosition().distanceTo(me.getTargetNode().getPosition());
            double carX = me.getX();
            double carY = me.getY();
            double targetX = me.getTargetNode().getPosition().getX();
            double targetY = me.getTargetNode().getPosition().getY();
            isInsideIntersection = Math.abs(carX - targetX) < boxSize && Math.abs(carY - targetY) < boxSize;
        }

        // =========================================================================
        // 🚀 PHẦN 1: NORMAL - DỪNG CHỜ ĐÈN ĐỎ
        // =========================================================================
        boolean isStoppingForLight = false;
        double stopLine = boxSize + (me.getLength() / 2.0);

        if (me.getTargetNode() != null && me.getTargetNode().getTrafficController() != null) {
            TrafficController controller = me.getTargetNode().getTrafficController();
            if (!controller.getLights().isEmpty()) {
                int lightIndex = (Math.abs(me.getY() - me.getTargetNode().getPosition().getY()) >
                        Math.abs(me.getX() - me.getTargetNode().getPosition().getX())) ? 1 : 0;
                if (lightIndex >= controller.getLights().size()) lightIndex = 0;

                LightState currentState = controller.getLights().get(lightIndex).getCurrentState();
                double dxToTarget = me.getTargetNode().getPosition().getX() - me.getX();
                double dyToTarget = me.getTargetNode().getPosition().getY() - me.getY();
                boolean isTargetInFront = (dxToTarget * carDirX + dyToTarget * carDirY) > 0;

                if (!isInsideIntersection && isTargetInFront &&
                        (currentState == LightState.RED || currentState == LightState.YELLOW)) {
                    if (distToNode <= stopLine + 80.0) {
                        isStoppingForLight = true;
                        if (distToNode <= stopLine + 40.0) { // Cách vạch 40px thì phanh chết
                            targetAcceleration = -3.0;
                            targetMaxSpeed = 0;
                        } else {
                            targetAcceleration = -0.5;
                            targetMaxSpeed = me.getBaseMaxSpeed() * 0.3;
                        }
                    }
                }
            }
        }

        // =========================================================================
        // 🚀 PHẦN 2: RADAR TIA CHIẾU THẲNG & BẢNG LÀN ẢO
        // =========================================================================
        boolean obstacleAhead = false;
        boolean mustYieldAtIntersection = false;
        double minDistance = Double.MAX_VALUE;
        Vehicle vehicleAhead = null;
        int myLane = me.getCurrentLane();

        boolean[] laneBlocked = {false, false, false}; // Mảng khảo sát làn đường

        for (Vehicle other : allVehicles) {
            if (other == me) continue;

            double dx = other.getX() - me.getX();
            double dy = other.getY() - me.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 150.0) continue;

            // =========================================================
            // 🔥 THUẬT TOÁN LỌC XE NGƯỢC CHIỀU (CHỐNG NHIỄU LÀN 0) 🔥
            // =========================================================
            double otherDirX = Math.cos(Math.toRadians(other.getAngle()));
            double otherDirY = Math.sin(Math.toRadians(other.getAngle()));

            // Tích vô hướng (1.0 = cùng chiều, 0 = vuông góc, -1.0 = ngược chiều)
            double directionDotProduct = (carDirX * otherDirX) + (carDirY * otherDirY);

            // Nếu 2 xe đang đi ngược hướng nhau (góc > 120 độ) -> Chắc chắn ở làn đối diện
            if (directionDotProduct < -0.5) {
                continue; // Bỏ qua ngay lập tức, không thèm nhận làm vật cản!
            }
            // =========================================================

            double forwardDist = dx * carDirX + dy * carDirY;
            double sideDist = dx * (-carDirY) + dy * carDirX;
            double angleDiff = Math.abs(me.getAngle() - other.getAngle());
            boolean isSameDirection = (angleDiff < 45 || angleDiff > 315);

            // Cập nhật mảng làn đường bị chặn
            if (isSameDirection && forwardDist > -10.0 && forwardDist < 100.0) {
                int otherLane = other.getCurrentLane();
                if (otherLane >= 0 && otherLane < 3) {
                    laneBlocked[otherLane] = true;
                }
            }

            // =========================================================
            // 🚨 ĐÃ SỬA: Bắt vật cản chiếu thẳng mặt (Radar co giãn theo kích thước xe)
            // =========================================================
            if (isSameDirection && forwardDist > 0 && forwardDist < 100.0) {
                double lateralSafe = (me.getWidth() / 2.0) + (other.getWidth() / 2.0) + 4.0;
                if (Math.abs(sideDist) < lateralSafe) {
                    if (forwardDist < minDistance) {
                        minDistance = forwardDist;
                        obstacleAhead = true;
                        vehicleAhead = other;
                    }
                }
            }

            // Luật nhường đường ngã tư
            if (me.getTargetNode() != null && me.getTargetNode() == other.getTargetNode()) {
                if (distToNode < 80.0 && dist < 60.0) {
                    int myPriority = getTurnPriority(me);
                    int otherPriority = getTurnPriority(other);
                    boolean shouldYield = false;

                    if (myPriority < otherPriority) shouldYield = true;
                    else if (myPriority == otherPriority && System.identityHashCode(me) < System.identityHashCode(other)) {
                        shouldYield = true;
                    }

                    if (shouldYield) {
                        mustYieldAtIntersection = true;
                        minDistance = Math.min(minDistance, dist);
                    }
                }
            }
        }

        // =========================================================================
        // 🚀 PHẦN 3: LÁCH LÀN VÀ PHANH CHỐNG ĐÈ XE
        // =========================================================================
        if (mustYieldAtIntersection) {
            targetAcceleration = -3.0;
            targetMaxSpeed = 0;
            me.stuckTime = 0;
        }
        else if (obstacleAhead && vehicleAhead != null) {
            double safeDist = (me.getLength() / 2.0) + (vehicleAhead.getLength() / 2.0);
            boolean isChangingLane = false;

            // Tìm làn trống để lách
            if (!isInsideIntersection && distToNode > 30.0) {
                if (myLane == 1) {
                    if (!laneBlocked[0]) { me.changeLane(0); isChangingLane = true; }
                    else if (!laneBlocked[2]) { me.changeLane(2); isChangingLane = true; }
                } else if (myLane == 0) {
                    if (!laneBlocked[1]) { me.changeLane(1); isChangingLane = true; }
                } else if (myLane == 2) {
                    if (!laneBlocked[1]) { me.changeLane(1); isChangingLane = true; }
                }
            }

            // 🚨 SỬA LỖI ĐÈ NHAU TẠI ĐÂY
            if (minDistance <= safeDist + 5.0) {
                targetAcceleration = -5.0; // Phanh cháy lốp
                targetMaxSpeed = 0;        // KHÔNG TIẾN LÊN KHI ĐANG SÁT ĐÍT
            } else if (minDistance <= safeDist + 25.0) {
                if (isChangingLane) {
                    targetAcceleration = 0.0; // Giữ nguyên tốc độ trượt ngang
                    targetMaxSpeed = vehicleAhead.getSpeed();
                } else {
                    targetAcceleration = -2.0;
                    targetMaxSpeed = vehicleAhead.getSpeed();
                }
            } else if (minDistance <= safeDist + 50.0) {
                targetAcceleration = -0.5;
                targetMaxSpeed = vehicleAhead.getSpeed() + 1.0;
            }
        }

        if (isStoppingForLight && distToNode <= stopLine + 40.0) {
            targetMaxSpeed = 0;
            targetAcceleration = -3.0;
        }

        // =========================================================================
        // 🚀 PHẦN 4: CẢM BIẾN VA CHẠM KHẨN CẤP + BỘ GIẢI QUYẾT DEADLOCK
        // =========================================================================
        for (Vehicle other : allVehicles) {
            if (other == me) continue;

            double dx = other.getX() - me.getX();
            double dy = other.getY() - me.getY();

            double forwardDist = dx * carDirX + dy * carDirY;
            double sideDist = dx * (-carDirY) + dy * carDirX;

            double safeLength = (me.getLength() / 2.0) + (other.getLength() / 2.0) + 1.0;
            double safeWidth = (me.getWidth() / 2.0) + (other.getWidth() / 2.0) + 1.0;

            // NẾU 2 XE CHẠM NHAU (Lún vào Hitbox hình chữ nhật của nhau)
            if (Math.abs(forwardDist) < safeLength && Math.abs(sideDist) < safeWidth) {

                // Lấy hướng của xe kia để xét xem đâm kiểu gì
                double otherDirX = Math.cos(Math.toRadians(other.getAngle()));
                double otherDirY = Math.sin(Math.toRadians(other.getAngle()));
                double dotP = (carDirX * otherDirX) + (carDirY * otherDirY);

                boolean shouldIYield = false;

                if (dotP > 0.5) {
                    // TRƯỜNG HỢP 1: ĐI CÙNG CHIỀU (Đâm đít)
                    // Nếu xe kia nằm ở PHÍA TRƯỚC mặt mình (forwardDist > 0) -> Mình phải phanh.
                    if (forwardDist > 0) {
                        shouldIYield = true;
                    }
                } else {
                    // TRƯỜNG HỢP 2: CẮT NGANG NGÃ TƯ HOẶC NGƯỢC CHIỀU
                    // Đứa nào ID nhỏ hơn thì ngoan ngoãn phanh, nhường đứa kia lách qua.
                    if (System.identityHashCode(me) < System.identityHashCode(other)) {
                        shouldIYield = true;
                    }
                }

                if (shouldIYield) {
                    targetAcceleration = -5.0; // Phanh cháy lốp
                    targetMaxSpeed = 0;
                    break;
                }
            }
        }

        // =========================================================================
        // ÁP DỤNG THÔNG SỐ VÀO XE
        // =========================================================================
        if (targetMaxSpeed <= 0 && me.getSpeed() > 0) me.setMaxSpeed(0);
        else me.setMaxSpeed(targetMaxSpeed);
        me.setAcceleration(targetAcceleration);
    }

    private int getTurnPriority(Vehicle v) {
        if (v.getTurnDirection() == null) return 2;
        switch (v.getTurnDirection()) {
            case RIGHT: return 3;
            case STRAIGHT: return 2;
            case LEFT: return 1;
            default: return 0;
        }
    }
}