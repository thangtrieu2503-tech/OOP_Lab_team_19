package VehicleSystem.vehicle;

import VehicleSystem.behavior.DrivingStrategy;
import traffic.map.IntersectionNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Vehicle {
    private String id;
    private String type;
    private double x, y;
    private double baseSpeed;
    private double angle;

    private IntersectionNode lastNode;
    private IntersectionNode targetNode;
    private List<IntersectionNode> fullMap;
    private DrivingStrategy driver;
    private Random random = new Random();

    private final double LANE_OFFSET = 35.0; // Khoảng cách so với tâm đường

    public Vehicle(String id, String type, IntersectionNode start, IntersectionNode target, double speed, List<IntersectionNode> map, DrivingStrategy driver) {
        this.id = id;
        this.type = type;
        this.lastNode = start;
        this.targetNode = target;
        this.baseSpeed = speed;
        this.fullMap = map;
        this.driver = driver;

        // Khởi tạo tọa độ tại tâm ngã tư xuất phát + offset làn đường
        double[] offset = calculateOffset(lastNode, targetNode);
        this.x = lastNode.getWorldX() + offset[0];
        this.y = lastNode.getWorldY() + offset[1];
    }

    public void update(List<Vehicle> allVehicles, String currentLightColor) {
        if (driver != null) driver.drive(this, allVehicles, currentLightColor);
    }

    public void moveForward(double speed) {
        // Tọa độ mục tiêu chuẩn của làn đường
        double[] offset = calculateOffset(lastNode, targetNode);
        double targetX = targetNode.getWorldX() + offset[0];
        double targetY = targetNode.getWorldY() + offset[1];

        double dx = targetX - this.x;
        double dy = targetY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            this.x = targetX;
            this.y = targetY;
            pickNextNode();
        } else {
            // Xe di chuyển bình thường theo Vector
            this.x += (dx / dist) * speed;
            this.y += (dy / dist) * speed;
            this.angle = Math.toDegrees(Math.atan2(dy, dx));
        }
    }

    private void pickNextNode() {
        List<IntersectionNode> candidates = new ArrayList<>();
        for (IntersectionNode n : fullMap) {
            int dx = Math.abs(n.getGridX() - targetNode.getGridX());
            int dy = Math.abs(n.getGridY() - targetNode.getGridY());
            if ((dx + dy == 1) && n != lastNode) candidates.add(n);
        }

        lastNode = targetNode;
        targetNode = candidates.isEmpty() ? lastNode : candidates.get(random.nextInt(candidates.size()));
    }

    private double[] calculateOffset(IntersectionNode from, IntersectionNode to) {
        double dx = to.getGridX() - from.getGridX();
        double dy = to.getGridY() - from.getGridY();

        // Nếu đi NGANG (X thay đổi)
        if (dx > 0) return new double[]{0.0, LANE_OFFSET};  // Sang phải -> Lệch Y xuống
        if (dx < 0) return new double[]{0.0, -LANE_OFFSET}; // Sang trái -> Lệch Y lên

        // Nếu đi DỌC (Y thay đổi)
        if (dy > 0) return new double[]{-LANE_OFFSET, 0.0}; // Xuống dưới -> Lệch X sang trái
        if (dy < 0) return new double[]{LANE_OFFSET, 0.0};  // Lên trên -> Lệch X sang phải

        return new double[]{0.0, 0.0};
    }

    public void brake() { /* Đứng yên */ }
    public boolean isFinished() { return this.x < -200 || this.x > 1500 || this.y < -200 || this.y > 1500; }
    public double distanceTo(Vehicle o) { return Math.sqrt(Math.pow(x - o.x, 2) + Math.pow(y - o.y, 2)); }
    public double distanceToNextNode() { return Math.sqrt(Math.pow(targetNode.getWorldX() - x, 2) + Math.pow(targetNode.getWorldY() - y, 2)); }
    public boolean isTurning() { return false; } // Đơn giản hóa
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public String getId() { return id; }
    public String getType() { return type; }
    public double getBaseSpeed() { return baseSpeed; }
    public void setHonking(boolean h) {}
}