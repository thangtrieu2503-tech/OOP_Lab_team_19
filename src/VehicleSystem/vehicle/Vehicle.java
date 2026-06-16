package VehicleSystem.vehicle;

import VehicleSystem.behavior.DrivingStrategy;
import MapSystem.map.Intersection;
import MapSystem.math.Vector2D;
import java.util.List;

public class Vehicle {
    // --------------------------------------------------------
    // 1. THUỘC TÍNH VỊ TRÍ & HƯỚNG ĐI (Toán học Vector)
    // --------------------------------------------------------
    private double x;
    private double y;
    private double dirX;
    private double dirY;

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

    private double width;  // Chiều rộng xe
    private double length; // Chiều dài xe

    // Cập nhật Constructor thêm width và length
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
    }

    // Thêm Getters cho họa sĩ và radar xài
    public double getWidth() { return width; }
    public double getLength() { return length; }

    // ========================================================
    // CONSTRUCTOR
    // ========================================================
    public Vehicle(double startX, double startY, double baseMaxSpeed, DrivingStrategy driver) {
        this.x = startX;
        this.y = startY;
        this.baseMaxSpeed = baseMaxSpeed;
        this.maxSpeed = baseMaxSpeed;
        this.speed = 0;
        this.acceleration = 0;
        this.driver = driver;
    }

    // ========================================================
    // LUỒNG CHẠY CHÍNH (Game Loop gọi hàm này)
    // ========================================================
    public void update(List<Vehicle> allVehicles) {
        // 1. Bộ não suy nghĩ và ra lệnh (Đạp ga/phanh/bóp còi)
        if (driver != null) {
            driver.drive(this, allVehicles);
        }
        // 2. Động cơ vật lý thực thi lệnh
        updatePhysics();
    }

    // ========================================================
    // LÕI VẬT LÝ CƠ BẢN
    // ========================================================
    private void updatePhysics() {
        speed += acceleration;

        // Khóa tốc độ ở mức 0 (chống đi lùi) và xả phanh
        if (speed <= 0) {
            speed = 0;
            if (acceleration < 0) {
                acceleration = 0;
            }
        }

        // Giới hạn tốc độ không vượt quá mức cho phép
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        // Tịnh tiến tọa độ theo Vector hướng
        if (speed > 0) {
            this.x += this.dirX * speed;
            this.y += this.dirY * speed;
        }
    }

    // ========================================================
    // HỆ THỐNG ĐIỀU HƯỚNG VECTOR
    // ========================================================
    // Nhận mục tiêu mới và tự động bẻ vô lăng chĩa về mục tiêu đó
    public void setTargetNode(Intersection targetNode) {
        this.targetNode = targetNode;
        recalculateDirection();
    }

    // Cập nhật lại góc lái bằng Toán học Vector
    private void recalculateDirection() {
        if (targetNode == null) return;

        Vector2D currentPos = this.getPosition();
        Vector2D targetPos = targetNode.getPosition();

        Vector2D direction = targetPos.subtract(currentPos);
        Vector2D normalizedDir = direction.normalize();

        this.dirX = normalizedDir.getX();
        this.dirY = normalizedDir.getY();
    }

    // Cảm biến check tới đích (Sai số < 5 pixel)
    public boolean hasReachedTarget() {
        if (targetNode == null) return false;
        return this.getPosition().distanceTo(targetNode.getPosition()) < 5.0;
    }

    // ========================================================
    // HỆ THỐNG GIAO TIẾP & VĂN HÓA GIAO THÔNG
    // ========================================================
    public void honkAt(Vehicle frontCar) {
        if (frontCar != null) {
            frontCar.receiveHonk();
        }
    }

    public void receiveHonk() {
        this.isRequestedToYield = true;
    }

    public boolean needsToYield() { return isRequestedToYield; }

    public void resetYieldFlag() { this.isRequestedToYield = false; }

    public void changeLane() {
        // Logic tịnh tiến vector vuông góc để dạt xe sang bên cạnh sẽ được bổ sung sau
        System.out.println("Xe đang đánh lái chuyển làn...");
    }

    // ========================================================
    // GETTERS & SETTERS CƠ BẢN
    // ========================================================
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; }
    public void setMaxSpeed(double maxSpeed) { this.maxSpeed = maxSpeed; }

    public double getBaseMaxSpeed() { return this.baseMaxSpeed; }
    public Intersection getTargetNode() { return this.targetNode; }
    public Vector2D getPosition() { return new Vector2D(this.x, this.y); }
    public double getSpeed() { return this.speed; }
}