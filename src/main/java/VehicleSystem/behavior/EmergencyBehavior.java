package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.vehicle.Vehicle.TurnDirection;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;

import java.util.List;

public class EmergencyBehavior implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        double targetMaxSpeed = me.getBaseMaxSpeed() * 1.5; // Xe ưu tiên đi nhanh hơn
        double targetAcceleration = 0.08;
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
        // 🚀 PHẦN 1: EMERGENCY - VƯỢT ĐÈN NHƯNG ĐI CHẬM
        // =========================================================================
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
                    double stopLine = boxSize + (me.getLength() / 2.0);
                    if (distToNode <= stopLine + 60.0) {
                        targetMaxSpeed = me.getBaseMaxSpeed() * 0.6; // KHÔNG DỪNG, CHỈ GIẢM TỐC
                    }
                }
            }
        }

        // =========================================================================
        // 🚀 PHẦN 2: RADAR TIA CHIẾU THẲNG & HÚ CÒI
        // =========================================================================
        boolean obstacleAhead = false;
        double minDistance = Double.MAX_VALUE;
        Vehicle vehicleAhead = null;
        int myLane = me.getCurrentLane();

        boolean[] laneBlocked = {false, false, false}; // Mảng khảo sát làn đường

        for (Vehicle other : allVehicles) {
            if (other == me) continue;

            double dx = other.getX() - me.getX();
            double dy = other.getY() - me.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist > 180.0) continue; // Radar xa hơn

            double forwardDist = dx * carDirX + dy * carDirY;
            double sideDist = dx * (-carDirY) + dy * carDirX;
            double angleDiff = Math.abs(me.getAngle() - other.getAngle());
            boolean isSameDirection = (angleDiff < 45 || angleDiff > 315);

            // TIA CÒI HÚ: ÉP XE KHÁC DẠT LÀN (Chỉ có ở Emergency)
            if (isSameDirection && forwardDist > 0 && forwardDist < 150.0) {
                if (Math.abs(sideDist) < 25.0) {
                    other.setRequestedToYield(true);
                }
            }

            // Cập nhật mảng làn bị chặn
            if (isSameDirection && forwardDist > -10.0 && forwardDist < 120.0) {
                int otherLane = other.getCurrentLane();
                if (otherLane >= 0 && otherLane < 3) {
                    laneBlocked[otherLane] = true;
                }
            }

            // Bắt vật cản chiếu thẳng mặt
            if (isSameDirection && forwardDist > 0 && forwardDist < 120.0) {
                if (Math.abs(sideDist) < 18.0) {
                    if (forwardDist < minDistance) {
                        minDistance = forwardDist;
                        obstacleAhead = true;
                        vehicleAhead = other;
                    }
                }
            }

            // Xử lý 2 xe ưu tiên đụng nhau
            if (me.getTargetNode() != null && me.getTargetNode() == other.getTargetNode()) {
                if (distToNode < 60.0 && dist < 50.0) {
                    if (System.identityHashCode(me) < System.identityHashCode(other)) {
                        obstacleAhead = true;
                        vehicleAhead = other;
                        minDistance = Math.min(minDistance, dist);
                    }
                }
            }
        }

        // =========================================================================
        // 🚀 PHẦN 3: LÁCH LÀN VÀ PHANH CHỐNG ĐÈ XE
        // =========================================================================
        if (obstacleAhead && vehicleAhead != null) {
            double safeDist = (me.getLength() / 2.0) + (vehicleAhead.getLength() / 2.0);
            boolean isChangingLane = false;

            // Tìm làn trống để lách (Bất chấp ngã tư)
            if (!isInsideIntersection) {
                if (myLane == 1) {
                    if (!laneBlocked[0]) { me.changeLane(0); isChangingLane = true; }
                    else if (!laneBlocked[2]) { me.changeLane(2); isChangingLane = true; }
                } else if (myLane == 0) {
                    if (!laneBlocked[1]) { me.changeLane(1); isChangingLane = true; }
                } else if (myLane == 2) {
                    if (!laneBlocked[1]) { me.changeLane(1); isChangingLane = true; }
                }
            }

            // 🚨 SỬA LỖI ĐÈ NHAU
            if (minDistance <= safeDist + 5.0) {
                targetAcceleration = -5.0;
                targetMaxSpeed = 0; // Kể cả xe ưu tiên, nếu sát đít quá cũng phải dừng ga tiến lên
            } else if (minDistance <= safeDist + 25.0) {
                if (isChangingLane) {
                    targetAcceleration = 0.0;
                    targetMaxSpeed = vehicleAhead.getSpeed();
                } else {
                    targetAcceleration = -2.0;
                    targetMaxSpeed = vehicleAhead.getSpeed();
                }
            } else if (minDistance <= safeDist + 60.0) {
                targetAcceleration = -0.5;
                targetMaxSpeed = Math.min(targetMaxSpeed, vehicleAhead.getSpeed() + 1.0);
            }
        }

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