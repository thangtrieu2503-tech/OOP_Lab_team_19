package VehicleSystem.vehicle;

import VehicleSystem.behavior.DrivingStrategy;
import MapSystem.map.Intersection;
import MapSystem.math.Vector2D;
import java.util.List;

public abstract class Vehicle {
    // Định nghĩa các hướng rẽ tại ngã tư
    public enum TurnDirection {
        STRAIGHT, LEFT, RIGHT
    }

    // --------------------------------------------------------
    // 1. THUỘC TÍNH VỊ TRÍ & HƯỚNG ĐI (Toán học Vector)
    // --------------------------------------------------------
    private double x;
    private double y;

    // Hai biến này dùng để lưu hướng của cái "Đường"
    private double dirX;
    private double dirY;

    // HỆ THỐNG BẺ LÁI
    protected double currentAngle = 0; // Hướng đầu xe hiện tại (radian)
    protected double turnSpeed = 0.2; // Tốc độ bẻ lái

    // --------------------------------------------------------
    // KÍCH THƯỚC XE VÀ ĐỘ LỆCH LÀN (Hệ thống làn đường)
    // --------------------------------------------------------
    protected double width;
    protected double length;
    protected double laneOffsetX = 0;
    protected double laneOffsetY = 0;

    protected double[] laneDistances = {14.0, 40.0, 66.0};
    protected int currentLane = 2; // Mặc định sinh ra ở làn số 2

    // --------------------------------------------------------
    // 2. THUỘC TÍNH VẬT LÝ & ĐỘNG CƠ
    // --------------------------------------------------------
    private double speed;
    private double baseMaxSpeed;
    private double maxSpeed;
    private double acceleration;

    // --------------------------------------------------------
    // 3. BỘ NÃO & MỤC TIÊU
    // --------------------------------------------------------
    private DrivingStrategy driver;
    private Intersection targetNode;

    // --------------------------------------------------------
    // 4. GIAO TIẾP VÀ NHƯỜNG ĐƯỜNG
    // --------------------------------------------------------
    private boolean isRequestedToYield = false;
    private Intersection previousNode;

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public Vehicle(double startX, double startY, double width, double length, double baseMaxSpeed, DrivingStrategy driver) {
        this.x = startX;
        this.y = startY;
        this.width = width;
        this.length = length;
        this.baseMaxSpeed = baseMaxSpeed;
        this.maxSpeed = baseMaxSpeed;
        this.speed = 0;
        this.acceleration = 0;
        this.driver = driver;

        // Mặc định cho xe hướng thẳng xuống
        this.currentAngle = Math.PI / 2;
    }

    // ========================================================
    // LUỒNG CHẠY CHÍNH (Game Loop gọi hàm này)
    // ========================================================
    public void update(List<Vehicle> allVehicles) {
        if (driver != null) {
            driver.drive(this, allVehicles);
        }
        updatePhysics();
    }

    public void setPreviousNode(Intersection previousNode) { this.previousNode = previousNode; }
    public Intersection getPreviousNode() { return this.previousNode; }

    // ========================================================
    // THUẬT TOÁN XÁC ĐỊNH HƯỚNG RẼ TIẾP THEO
    // ========================================================
    public TurnDirection getTurnDirection() {
        if (previousNode == null || targetNode == null) {
            return TurnDirection.STRAIGHT;
        }

        // Vector hướng đi cũ (Từ ngã tư trước đến ngã tư hiện tại)
        double oldDx = targetNode.getPosition().getX() - previousNode.getPosition().getX();
        double oldDy = targetNode.getPosition().getY() - previousNode.getPosition().getY();

        // Tính toán tích vô hướng chéo để xác định hướng rẽ trên màn hình (Y hướng xuống)
        double crossProduct = oldDx * this.dirY - oldDy * this.dirX;

        if (crossProduct > 0.1) {
            return TurnDirection.RIGHT; // Rẽ phải
        } else if (crossProduct < -0.1) {
            return TurnDirection.LEFT;  // Rẽ trái
        }

        return TurnDirection.STRAIGHT; // Đi thẳng
    }

    // ========================================================
    // LÕI VẬT LÝ CƠ BẢN + LOGIC BẺ LÁI MƯỢT
    // ========================================================
    private void updatePhysics() {
        speed += acceleration;

        if (speed <= 0) {
            speed = 0;
            if (acceleration < 0) acceleration = 0;
        }

        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        if (speed > 0 && targetNode != null) {
            double targetX = targetNode.getPosition().getX() + laneOffsetX;
            double targetY = targetNode.getPosition().getY() + laneOffsetY;

            double targetAngle = Math.atan2(targetY - this.y, targetX - this.x);
            double angleDiff = targetAngle - this.currentAngle;

            while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
            while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

            if (Math.abs(angleDiff) > turnSpeed) {
                this.currentAngle += Math.signum(angleDiff) * turnSpeed;
            } else {
                this.currentAngle = targetAngle;
            }

            this.x += Math.cos(this.currentAngle) * speed;
            this.y += Math.sin(this.currentAngle) * speed;
        }
    }

    // ========================================================
    // HỆ THỐNG ĐIỀU HƯỚNG VECTOR
    // ========================================================
    public void setTargetNode(Intersection targetNode) {
        this.targetNode = targetNode;
        recalculateDirection();
    }

    public void setLaneOffset(double ox, double oy) {
        this.laneOffsetX = ox;
        this.laneOffsetY = oy;
        recalculateDirection();
    }

    private void recalculateDirection() {
        if (targetNode == null) return;

        double currentCenterX = this.x - laneOffsetX;
        double currentCenterY = this.y - laneOffsetY;

        double dx = targetNode.getPosition().getX() - currentCenterX;
        double dy = targetNode.getPosition().getY() - currentCenterY;

        if (Math.abs(dx) > Math.abs(dy)) {
            this.dirX = Math.signum(dx);
            this.dirY = 0.0;
        } else {
            this.dirX = 0.0;
            this.dirY = Math.signum(dy);
        }

        updateLaneOffset();

        this.x = currentCenterX + this.laneOffsetX;
        this.y = currentCenterY + this.laneOffsetY;
    }

    private void updateLaneOffset() {
        double laneDistance = laneDistances[this.currentLane];
        this.laneOffsetX = -this.dirY * laneDistance;
        this.laneOffsetY = this.dirX * laneDistance;
    }

    public boolean hasReachedTarget() {
        if (targetNode == null) return false;

        // Điểm neo thực tế trong làn đường
        Vector2D realTarget = new Vector2D(
                targetNode.getPosition().getX() + laneOffsetX,
                targetNode.getPosition().getY() + laneOffsetY
        );

        return this.getPosition().distanceTo(realTarget) < 5.0;
    }

    // ========================================================
    // HỆ THỐNG GIAO TIẾP & CHUYỂN LÀN
    // ========================================================
    public void honkAt(Vehicle frontCar) { if (frontCar != null) frontCar.receiveHonk(); }
    public void receiveHonk() { this.isRequestedToYield = true; }
    public boolean needsToYield() { return isRequestedToYield; }
    public void resetYieldFlag() { this.isRequestedToYield = false; }

    public void changeLane(int targetLane) {
        if (targetLane >= 0 && targetLane < laneDistances.length) {
            this.currentLane = targetLane;
            updateLaneOffset();
        }
    }

    // ========================================================
    // GETTERS & SETTERS
    // ========================================================
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; }
    public void setMaxSpeed(double maxSpeed) { this.maxSpeed = maxSpeed; }
    public double getBaseMaxSpeed() { return this.baseMaxSpeed; }
    public Intersection getTargetNode() { return this.targetNode; }
    public Vector2D getPosition() { return new Vector2D(this.x, this.y); }
    public double getSpeed() { return this.speed; }
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getWidth() { return this.width; }
    public double getLength() { return this.length; }
    public int getCurrentLane() { return this.currentLane; }

    public double getAngle() {
        return Math.toDegrees(this.currentAngle);
    }

    public String getType() {
        String className = this.getClass().getSimpleName().toUpperCase();
        if (className.equals("FIRETRUCK")) return "FIRE_TRUCK";
        return className;
    }
}