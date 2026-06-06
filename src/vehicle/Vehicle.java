package vehicle;

import behavior.DrivingStrategy;
import java.util.List;

public abstract class Vehicle {
    protected double x, y;
    protected double speed;
    protected double maxSpeed;
    protected double width, height;
    protected double angle = 0; // Góc xoay của xe (tính bằng độ để rẽ hướng)
    protected boolean isShiftingLane = false;
    protected DrivingStrategy drivingStrategy;

    // Danh sách các điểm mốc (Waypoints) tạo nên con đường (Ngang, dọc, chéo đều được)
    protected List<MyPoint> waypoints;
    protected int currentWaypointIndex = 0;

    public Vehicle(List<MyPoint> waypoints, double maxSpeed, DrivingStrategy strategy) {
        this.waypoints = waypoints;
        this.maxSpeed = maxSpeed;
        this.speed = maxSpeed * 0.5; // Xuất phát bằng 1/2 tốc độ max
        this.width = 40;  // Chiều dài đầu xe
        this.height = 20; // Chiều rộng hông xe
        this.drivingStrategy = strategy;

        // Xuất phát tại điểm mốc đầu tiên của con đường
        if (waypoints != null && !waypoints.isEmpty()) {
            this.x = waypoints.get(0).x;
            this.y = waypoints.get(0).y;
        }
    }

    // Hàm di chuyển bất chấp mọi loại đường: Ngang, Dọc, Chéo
    public void move() {
        if (waypoints == null || currentWaypointIndex >= waypoints.size()) return;

        // Nhắm tới điểm mốc tiếp theo
        MyPoint targetPoint = waypoints.get(currentWaypointIndex);

        double dx = targetPoint.x - this.x;
        double dy = targetPoint.y - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Nếu đã đến sát điểm mốc (dưới 5px) -> Nhắm sang điểm mốc tiếp theo
        if (distance < 5.0) {
            currentWaypointIndex++;
            return;
        }

        // TÍNH TOÁN HƯỚNG VÀ GÓC XOAY ĐẦU XE (Dùng hàm Math.atan2 huyền thoại)
        this.angle = Math.toDegrees(Math.atan2(dy, dx));

        // Tịnh tiến tọa độ X, Y theo vector hướng (Xe chạy dọc, chạy chéo mượt mà)
        this.x += (dx / distance) * this.speed;
        this.y += (dy / distance) * this.speed;
    }

    // --- Hệ thống Getter / Setter ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; } // Lấy góc xoay để JavaFX vẽ xoay hình
    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = Math.max(0, Math.min(speed, maxSpeed * 1.2)); }
    public double getMaxSpeed() { return maxSpeed; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public boolean isShiftingLane() { return isShiftingLane; }
    public void setShiftingLane(boolean shiftingLane) { this.isShiftingLane = shiftingLane; }
    public void applyAI(Vehicle front, boolean red, double dist, List<Vehicle> all) {
        if (this.drivingStrategy != null) this.drivingStrategy.updateMovement(this, front, red, dist, all);
    }
}