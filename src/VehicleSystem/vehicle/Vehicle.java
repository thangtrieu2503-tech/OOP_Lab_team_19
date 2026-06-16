package VehicleSystem.vehicle;

import VehicleSystem.behavior.DrivingStrategy;
import MapSystem.map.Intersection;
import MapSystem.math.Vector2D;
import java.util.List;

public abstract class Vehicle { // Nên để abstract vì ông có các class con như Car, Bus...
    // --------------------------------------------------------
    // 1. THUỘC TÍNH VỊ TRÍ & HƯỚNG ĐI (Toán học Vector)
    // --------------------------------------------------------
    private double x;
    private double y;
    private double dirX;
    private double dirY;

    // --------------------------------------------------------
    // MỚI: KÍCH THƯỚC XE VÀ ĐỘ LỆCH LÀN (Phục vụ UI và chia làn)
    // --------------------------------------------------------
    protected double width;
    protected double length;
    protected double laneOffsetX = 0;
    protected double laneOffsetY = 0;

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

    // ========================================================
    // CONSTRUCTOR (Đã bổ sung width và length)
    // ========================================================
    private Intersection previousNode;

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

    // Hàm này để Manager gọi vào để lưu lại ngã tư vừa đi qua
    public void setPreviousNode(Intersection previousNode) {
        this.previousNode = previousNode;
    }

    // Hàm này để Manager gọi vào để kiểm tra xem có phải ngã tư vừa rồi không
    public Intersection getPreviousNode() {
        return this.previousNode;
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
    // HỆ THỐNG ĐIỀU HƯỚNG VECTOR (Đã ép xe hướng vào làn)
    // ========================================================
    public void setTargetNode(Intersection targetNode) {
        this.targetNode = targetNode;
        recalculateDirection();
    }

    public void setLaneOffset(double ox, double oy) {
        this.laneOffsetX = ox;
        this.laneOffsetY = oy;
        recalculateDirection(); // Tính lại góc lái ngay khi bị ép sang làn khác
    }

    private void recalculateDirection() {
        if (targetNode == null) return;

        double currentCenterX = this.x - laneOffsetX;
        double currentCenterY = this.y - laneOffsetY;

        double dx = targetNode.getPosition().getX() - currentCenterX;
        double dy = targetNode.getPosition().getY() - currentCenterY;

        // Vẫn dùng trò "Khử sai số" để xe thẳng tắp
        if (Math.abs(dx) > Math.abs(dy)) {
            this.dirX = Math.signum(dx);
            this.dirY = 0.0;
        } else {
            this.dirX = 0.0;
            this.dirY = Math.signum(dy);
        }

        // ========================================================
        // 🛠️ DẠT SANG LÀN BÊN PHẢI (Luật VN)
        // ========================================================
        double laneDistance = 40.0;

        // Công thức xoay 90 độ sang PHẢI:
        this.laneOffsetX = -this.dirY * laneDistance;
        this.laneOffsetY = this.dirX * laneDistance;

        this.x = currentCenterX + this.laneOffsetX;
        this.y = currentCenterY + this.laneOffsetY;
    }

    // Cảm biến check tới đích (Đã tính kèm offset)
    public boolean hasReachedTarget() {
        if (targetNode == null) return false;

        // Đích đến thực sự = Tâm ngã tư + Độ dạt làn trái
        Vector2D realTarget = new Vector2D(
                targetNode.getPosition().getX() + laneOffsetX,
                targetNode.getPosition().getY() + laneOffsetY
        );

        return this.getPosition().distanceTo(realTarget) < 5.0;
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
        System.out.println("Xe đang đánh lái chuyển làn...");
    }

    // ========================================================
    // GETTERS & SETTERS BỔ SUNG CHO CANVAS VẼ ĐỒ HỌA
    // ========================================================
    public void setAcceleration(double acceleration) { this.acceleration = acceleration; }
    public void setMaxSpeed(double maxSpeed) { this.maxSpeed = maxSpeed; }

    public double getBaseMaxSpeed() { return this.baseMaxSpeed; }
    public Intersection getTargetNode() { return this.targetNode; }
    public Vector2D getPosition() { return new Vector2D(this.x, this.y); }
    public double getSpeed() { return this.speed; }

    // Mấy hàm này bắt buộc phải có để class Canvas của ông gọi
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getWidth() { return this.width; }
    public double getLength() { return this.length; }

    // Tính góc vô lăng dựa trên dirX, dirY để vẽ xoay xe
    public double getAngle() {
        return Math.toDegrees(Math.atan2(this.dirY, this.dirX));
    }

    // Ép kiểu tên Class phục vụ cho switch-case lấy màu/sprite
    public String getType() {
        String className = this.getClass().getSimpleName().toUpperCase();
        if (className.equals("FIRETRUCK")) return "FIRE_TRUCK";
        return className;
    }
}