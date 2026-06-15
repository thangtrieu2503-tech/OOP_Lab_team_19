package VehicleSystem.vehicle;

import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.behavior.DrivingStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Vehicle {
    private String id;
    private String type;
    private double x, y;
    private double baseSpeed;
    private double angle;

    private Intersection lastNode;
    private Intersection targetNode;
    private RoadGraph fullMap;

    private DrivingStrategy driver;
    private Random random = new Random();

    private double currentTargetX;
    private double currentTargetY;
    private final double LANE_OFFSET = 35.0; // Khoảng cách lách sang phải

    public Vehicle(String id, String type, Intersection startNode, Intersection firstTarget, double baseSpeed, RoadGraph fullMap, DrivingStrategy driver) {
        this.id = id;
        this.type = type;
        this.lastNode = startNode;
        this.targetNode = firstTarget;
        this.baseSpeed = baseSpeed;
        this.fullMap = fullMap;
        this.driver = driver;

        // Tính toán lệch làn
        double[] offset = calculateLaneOffset(startNode, firstTarget);
        this.x = startNode.getPosition().getX() + offset[0];
        this.y = startNode.getPosition().getY() + offset[1];

        this.currentTargetX = firstTarget.getPosition().getX() + offset[0];
        this.currentTargetY = firstTarget.getPosition().getY() + offset[1];
    }

    public void update(List<Vehicle> allVehicles, String currentLightColor) {
        if (driver != null) {
            driver.drive(this, allVehicles, currentLightColor);
        } else {
            moveForward(baseSpeed); // Chạy dự phòng nếu driver bị lỗi
        }
    }

    // ==============================================================
    // HÀM CHẠY ĐÃ ĐƯỢC FIX LỖI TÀNG HÌNH VÀ BẺ LÁI XỊN
    // ==============================================================
    public void moveForward(double currentSpeed) {
        double dx = currentTargetX - this.x;
        double dy = currentTargetY - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // NẾU XE ĐÃ CHẠM VÀO TÂM NGÃ TƯ -> TIẾN HÀNH RẼ
        if (distance <= currentSpeed) {

            // 1. Dò bản đồ để bốc random 1 hướng đi mới
            pickRandomTurn();

            // 2. Tính lại tọa độ của cái làn đường mới
            double[] newOffset = calculateLaneOffset(lastNode, targetNode);

            // 3. Snap (Gắn) xe vào đúng đầu làn đường mới luôn để tránh lỗi trôi tọa độ (NaN)
            this.x = lastNode.getPosition().getX() + newOffset[0];
            this.y = lastNode.getPosition().getY() + newOffset[1];

            // 4. Đặt mục tiêu thẳng tiến tới ngã tư tiếp theo
            this.currentTargetX = targetNode.getPosition().getX() + newOffset[0];
            this.currentTargetY = targetNode.getPosition().getY() + newOffset[1];

        } else {
            // ĐANG TRÊN ĐƯỜNG NHỰA -> TIẾP TỤC CHẠY TỊNH TIẾN
            this.x += (dx / distance) * currentSpeed;
            this.y += (dy / distance) * currentSpeed;

            // Xoay đầu xe theo hướng chạy
            this.angle = Math.toDegrees(Math.atan2(dy, dx));
        }
    }

    private void pickRandomTurn() {
        List<Intersection> possibleTurns = new ArrayList<>();
        List<Intersection> neighbors = fullMap.getNeighbors(targetNode);

        if (neighbors != null) {
            for (Intersection n : neighbors) {
                // Kiểm tra tọa độ: Cấm quay đầu lại đúng cái đường vừa đi ra
                if (n.getPosition().getX() != lastNode.getPosition().getX() ||
                        n.getPosition().getY() != lastNode.getPosition().getY()) {
                    possibleTurns.add(n);
                }
            }
        }

        Intersection nextNode;
        if (!possibleTurns.isEmpty()) {
            // Tung xúc xắc chọn hướng rẽ (Trái, Phải, Thẳng)
            nextNode = possibleTurns.get(random.nextInt(possibleTurns.size()));
        } else if (neighbors != null && !neighbors.isEmpty()) {
            nextNode = neighbors.get(0); // Nếu đi vào đường cụt thì buộc phải quay đầu
        } else {
            nextNode = lastNode; // Chống crash map
        }

        lastNode = targetNode;
        targetNode = nextNode;
    }

    private double[] calculateLaneOffset(Intersection from, Intersection to) {
        double fromX = from.getPosition().getX();
        double fromY = from.getPosition().getY();
        double toX = to.getPosition().getX();
        double toY = to.getPosition().getY();

        if (Math.abs(toX - fromX) > Math.abs(toY - fromY)) {
            // ĐI NGANG (Nên lách trục Y)
            if (toX > fromX) return new double[]{0.0, LANE_OFFSET};
            else return new double[]{0.0, -LANE_OFFSET};
        } else {
            // ĐI DỌC (Nên lách trục X)
            if (toY > fromY) return new double[]{-LANE_OFFSET, 0.0};
            else return new double[]{LANE_OFFSET, 0.0};
        }
    }

    public double distanceTo(Vehicle other) {
        double dx = this.x - other.getX();
        double dy = this.y - other.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double distanceToNextNode() {
        double dx = currentTargetX - this.x;
        double dy = currentTargetY - this.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public String getId() { return id; }
    public String getType() { return type; }
    public double getBaseSpeed() { return baseSpeed; }
    public void brake() {}
    public boolean isFinished() { return false; }
}