package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;

import java.util.List;

public class AggressiveBehavior implements DrivingStrategy {

    private void handleYielding(Vehicle me) {
        if (!me.isRequestedToYield()) return;

        int myLane = me.getCurrentLane();
        boolean canMoveLeft = (myLane > 0);
        boolean canMoveRight = (myLane < 2);

        if (canMoveLeft) {
            me.changeLane(myLane - 1);
        } else if (canMoveRight) {
            me.changeLane(myLane + 1);
        }

        me.setRequestedToYield(false);
    }

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        handleYielding(me);

        boolean isRedLightAhead = false;
        double targetAcceleration = 0.04;

        // Trẻ trâu thì đạp ga nhanh hơn 20%
        double targetMaxSpeed = me.getBaseMaxSpeed() * 1.2;

        double carDirX = Math.cos(Math.toRadians(me.getAngle()));
        double carDirY = Math.sin(Math.toRadians(me.getAngle()));

        boolean isInsideIntersection = false;
        double distToNode = 0.0;
        double boxSize = 90.0;

        if (me.getTargetNode() != null) {
            distToNode = me.getPosition().distanceTo(me.getTargetNode().getPosition());
            double carX = me.getX();
            double carY = me.getY();
            double targetX = me.getTargetNode().getPosition().getX();
            double targetY = me.getTargetNode().getPosition().getY();
            boolean inTargetBox = Math.abs(carX - targetX) < boxSize && Math.abs(carY - targetY) < boxSize;
            isInsideIntersection = inTargetBox;
        }

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

                if (!isInsideIntersection && isTargetInFront
                        && (currentState == LightState.RED || currentState == LightState.YELLOW)) {

                    double stopLine = boxSize + (me.getLength() / 2.0);
                    if (distToNode <= stopLine + 60.0) {
                        isRedLightAhead = true;
                        if (distToNode <= stopLine + 5.0) {
                            targetAcceleration = -2.0;
                            targetMaxSpeed = 0;
                            if (me.getSpeed() > 0) me.setMaxSpeed(0);
                        } else {
                            targetAcceleration = -0.3;
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

        boolean obstacleAhead = false;
        double minDistance = Double.MAX_VALUE;
        Vehicle vehicleAhead = null;

        int myLane = me.getCurrentLane();
        boolean canMoveLeft = (myLane > 0);
        boolean canMoveRight = (myLane < 2);

        for (Vehicle other : allVehicles) {
            if (other == me) continue;

            double dx = other.getX() - me.getX();
            double dy = other.getY() - me.getY();
            double dist = Math.sqrt(dx * dx + dy * dy);

            if (dist > 120.0) continue;

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

            if (me.getTargetNode() != null && me.getTargetNode() == other.getTargetNode()) {
                if (distToNode < 90.0 && dist < 60.0) {
                    if (System.identityHashCode(me) < System.identityHashCode(other)) {
                        obstacleAhead = true;
                        vehicleAhead = other;
                        minDistance = Math.min(minDistance, dist);
                    }
                }
            }

            double forwardDist = dx * carDirX + dy * carDirY;
            double sideDist = dx * (-carDirY) + dy * carDirX;

            if (forwardDist > 0 && forwardDist < 80.0) {
                double lateralSafe = (me.getWidth() / 2.0) + (other.getWidth() / 2.0) + 3.0;
                if (Math.abs(sideDist) < lateralSafe) {
                    if (dist < minDistance) {
                        minDistance = dist;
                        obstacleAhead = true;
                        vehicleAhead = other;
                    }
                }
            }

            if (forwardDist > -25.0 && forwardDist < 50.0) {
                if (sideDist > 12.0 && sideDist < 60.0) canMoveRight = false;
                if (sideDist < -12.0 && sideDist > -60.0) canMoveLeft = false;
            }
        }

        if (obstacleAhead && vehicleAhead != null) {
            double safeDist = (me.getLength() / 2.0) + (vehicleAhead.getLength() / 2.0);

            // 🚨 SỬA Ở ĐÂY: Chỉ bóp còi khi khoảng cách gần VÀ xe trước đang di chuyển
            if (minDistance <= safeDist + 48.0 && vehicleAhead.getSpeed() > 0.1 && me.getSpeed() > 0.5) {
                vehicleAhead.receiveHonk();

                String typeName = me.getClass().getSimpleName();
                if (typeName.equals("Car") || typeName.equals("Bus")) {
                    UI.SoundManager.playCarHorn();
                }
            }

            if (minDistance <= safeDist + 5.0) {
                targetAcceleration = -3.0;
                targetMaxSpeed = 0;
                if (me.getSpeed() > 0) me.setMaxSpeed(0);

                if (me.getSpeed() <= 0.1) {
                    if (me.stuckTime == 0) me.stuckTime = System.currentTimeMillis();
                    else if (System.currentTimeMillis() - me.stuckTime > 5000) {
                        me.isDead = true;
                        vehicleAhead.isDead = true;
                    }
                }
            } else if (minDistance <= safeDist + 25.0) {
                me.stuckTime = 0;
                targetAcceleration = -1.0;
                targetMaxSpeed = vehicleAhead.getSpeed();
            } else if (minDistance <= safeDist + 50.0) {
                me.stuckTime = 0;
                targetAcceleration = -0.2;
                targetMaxSpeed = Math.min(me.getBaseMaxSpeed(), vehicleAhead.getSpeed() + 0.5);
            }

            boolean isSafeToChangeLane = !isInsideIntersection && distToNode > 95.0;
            if (isSafeToChangeLane && me.getSpeed() > 0.1) {
                if (canMoveLeft) {
                    me.changeLane(myLane - 1);
                } else if (canMoveRight) {
                    me.changeLane(myLane + 1);
                }
            }
        } else {
            me.stuckTime = 0;
        }

        me.setMaxSpeed(targetMaxSpeed);
        me.setAcceleration(targetAcceleration);
    }
}