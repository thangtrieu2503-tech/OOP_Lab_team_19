package VehicleSystem.behavior;

import VehicleSystem.vehicle.Vehicle;
import MapSystem.light.LightState;
import MapSystem.light.TrafficController;
import java.util.List;

public class NormalBehavior implements DrivingStrategy {

    @Override
    public void drive(Vehicle me, List<Vehicle> allVehicles) {
        boolean isRedLightAhead = false;
        double targetAcceleration = 0.04;
        double targetMaxSpeed = me.getBaseMaxSpeed();

        double carDirX = Math.cos(Math.toRadians(me.getAngle()));
        double carDirY = Math.sin(Math.toRadians(me.getAngle()));

        // =========================================================================
        // 🚀 THÔNG TIN DÙNG CHUNG: VỊ TRÍ & KHOẢNG CÁCH TỚI NGÃ TƯ
        // =========================================================================
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

            boolean inPrevBox = false;
            if (me.getPreviousNode() != null) {
                double prevX = me.getPreviousNode().getPosition().getX();
                double prevY = me.getPreviousNode().getPosition().getY();
                inPrevBox = Math.abs(carX - prevX) < boxSize && Math.abs(carY - prevY) < boxSize;
            }

            isInsideIntersection = inTargetBox || inPrevBox;
        }

        // =========================================================================
        // 🚀 PHẦN 1: LOGIC ĐÈN GIAO THÔNG (ĐÃ SỬA: NGHIÊM CẤM VƯỢT ĐÈN)
        // =========================================================================
        if (me.getTargetNode() != null && me.getTargetNode().getTrafficController() != null) {
            TrafficController controller = me.getTargetNode().getTrafficController();

            if (!controller.getLights().isEmpty()) {
                int lightIndex = (Math.abs(me.getY() - me.getTargetNode().getPosition().getY()) >
                        Math.abs(me.getX() - me.getTargetNode().getPosition().getX())) ? 1 : 0;

                if (lightIndex >= controller.getLights().size()) {
                    lightIndex = 0;
                }

                LightState currentState = controller.getLights().get(lightIndex).getCurrentState();

                double dxToTarget = me.getTargetNode().getPosition().getX() - me.getX();
                double dyToTarget = me.getTargetNode().getPosition().getY() - me.getY();
                boolean isTargetInFront = (dxToTarget * carDirX + dyToTarget * carDirY) > 0;

                // 🚦 FIX TRIỆT ĐỂ: Bỏ hoàn toàn luật làn số 2. Đèn đỏ/vàng là PHẢI DỪNG!
                if (!isInsideIntersection && isTargetInFront
                        && (currentState == LightState.RED || currentState == LightState.YELLOW)) {

                    double stopLine = boxSize + (me.getLength() / 2.0);

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
                            targetAcceleration = -0.2;
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

        // =========================================================================
        // 🚀 PHẦN 2: RADAR TỔNG HỢP & BSM CẢNH BÁO ĐIỂM MÙ (GIỮ NGUYÊN ĐỘ NHẠY SONG SONG)
        // =========================================================================
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

            if (dist > 140.0) continue;

            double forwardDist = dx * carDirX + dy * carDirY;
            double sideDist = dx * (-carDirY) + dy * carDirX;

            if (forwardDist > 0) {
                double otherAngle = other.getAngle();
                double myAngle = me.getAngle();

                double lateralSafe;
                double angleDiffDeg = Math.abs(myAngle - otherAngle) % 360;
                boolean isParallel = (angleDiffDeg < 20) || (angleDiffDeg > 160 && angleDiffDeg < 200);

                if (isParallel) {
                    // Xe đi song song (Dàn hàng 3 xe khít rịt)
                    lateralSafe = (me.getWidth() / 2.0) + (other.getWidth() / 2.0) + 2.0;
                } else {
                    // Xe đi vuông góc (Tránh mù T-bone ở ngã tư)
                    lateralSafe = (me.getWidth() / 2.0) + (other.getLength() / 2.0) + 5.0;
                }

                if (Math.abs(sideDist) < lateralSafe) {
                    if (dist < minDistance) {
                        minDistance = dist;
                        obstacleAhead = true;
                        vehicleAhead = other;
                    }
                }
            }

            if (forwardDist > -40.0 && forwardDist < 80.0) {
                if (sideDist > 15.0 && sideDist < 55.0) canMoveRight = false;
                if (sideDist < -15.0 && sideDist > -55.0) canMoveLeft = false;
            }
        }

        // =========================================================================
        // 🚀 PHẦN 3: RA QUYẾT ĐỊNH CHUYỂN LÀN HOẶC BÁM ĐUÔI (GIỮ NGUYÊN)
        // =========================================================================
        if (obstacleAhead && vehicleAhead != null) {
            double otherHitboxVertical = Math.max(vehicleAhead.getLength(), vehicleAhead.getWidth());
            double safeDist = (me.getLength() / 2.0) + (otherHitboxVertical / 2.0);

            boolean isSafeToChangeLane = !isInsideIntersection && distToNode > 100.0;

            if (isSafeToChangeLane && minDistance > safeDist + 15.0 && me.getSpeed() > 0.5) {
                if (canMoveLeft) {
                    me.changeLane(myLane - 1);
                } else if (canMoveRight) {
                    me.changeLane(myLane + 1);
                }
            }

            if (minDistance <= safeDist + 10.0) {
                targetAcceleration = -2.0;
                targetMaxSpeed = 0;
                if (me.getSpeed() > 0) me.setMaxSpeed(0);

                // 💀 LOGIC 5S BAY MÀU
                // 💀 LOGIC 5S BAY MÀU (CHẾT CHÙM)
                if (me.getSpeed() <= 0.1) {
                    if (me.stuckTime == 0) {
                        me.stuckTime = System.currentTimeMillis();
                    } else if (System.currentTimeMillis() - me.stuckTime > 5000) {
                        me.isDead = true; // Mình bay màu
                        // Kéo luôn thằng cản đường bốc hơi cùng!
                        if (vehicleAhead != null) {
                            vehicleAhead.isDead = true;
                        }
                    }
                }
            } else if (minDistance <= safeDist + 35.0) {
                me.stuckTime = 0;
                targetAcceleration = -0.3;
                targetMaxSpeed = Math.min(me.getBaseMaxSpeed(), vehicleAhead.getSpeed());
            }
        } else {
            me.stuckTime = 0;
        }

        // =========================================================================
        // 🚀 PHẦN 4: CHỐT THÔNG SỐ XUỐNG ĐỘNG CƠ
        // =========================================================================
        me.setMaxSpeed(targetMaxSpeed);
        me.setAcceleration(targetAcceleration);
    }
}