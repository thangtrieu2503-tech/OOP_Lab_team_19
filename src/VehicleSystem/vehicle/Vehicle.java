package VehicleSystem.vehicle;

import VehicleSystem.behavior.DrivingStrategy;
import MapSystem.map.Intersection;
import MapSystem.math.Vector2D;
import java.util.List;

public abstract class Vehicle {
    // --------------------------------------------------------
    // 1. THUỘC TÍNH VỊ TRÍ & HƯỚNG ĐI (Toán học Vector)
    // --------------------------------------------------------
    private double x;
    private double y;

    // Hai biến này giờ dùng để lưu hướng của cái "Đường"
    private double dirX;
    private double dirY;

    // 🛠️ HỆ THỐNG BẺ LÁI (Được đưa vào từ yêu cầu rẽ mượt)
    protected double currentAngle = 0; // Hướng đầu xe hiện tại (radian)
    protected double turnSpeed = 0.2; // Tốc độ bẻ lái

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

        // Mặc định cho xe hướng thẳng xuống (sẽ được chỉnh lại ngay khi có target)
        this.currentAngle = Math.PI / 2;
    }

    // ========================================================
    // LUỒNG CHẠY CHÍNH (Game Loop gọi hàm này)
    // ========================================================
    public void update(List<Vehicle> allVehicles) {
        // 1. Bộ não suy nghĩ và ra lệnh
        if (driver != null) {
            driver.drive(this, allVehicles);
        }
        // 2. Động cơ vật lý thực thi lệnh
        updatePhysics();
    }

    public void setPreviousNode(Intersection previousNode) { this.previousNode = previousNode; }
    public Intersection getPreviousNode() { return this.previousNode; }

    // ========================================================
    // LÕI VẬT LÝ CƠ BẢN + LOGIC BẺ LÁI MƯỢT
    // ========================================================
    private void updatePhysics() {
        speed += acceleration;

        // Khóa tốc độ
        if (speed <= 0) {
            speed = 0;
            if (acceleration < 0) acceleration = 0;
        }

        // Giới hạn max speed
        if (speed > maxSpeed) {
            speed = maxSpeed;
        }

        // 🛠️ LOGIC BẺ LÁI TỪ TỪ THAY VÌ ĐI THẲNG X, Y
        if (speed > 0 && targetNode != null) {
            // Điểm đến thực sự = Tâm ngã tư + Độ lệch làn
            double targetX = targetNode.getPosition().getX() + laneOffsetX;
            double targetY = targetNode.getPosition().getY() + laneOffsetY;

            // Tính góc mà cái xe cần phải hướng tới
            double targetAngle = Math.atan2(targetY - this.y, targetX - this.x);
            double angleDiff = targetAngle - this.currentAngle;

            // Khử lỗi xoay 360 độ
            while (angleDiff > Math.PI) angleDiff -= 2 * Math.PI;
            while (angleDiff < -Math.PI) angleDiff += 2 * Math.PI;

            // Xoay dần dần
            if (Math.abs(angleDiff) > turnSpeed) {
                this.currentAngle += Math.signum(angleDiff) * turnSpeed;
            } else {
                this.currentAngle = targetAngle; // Khóa cứng góc nếu đã sát
            }

            // Tịnh tiến theo góc Vô lăng
            this.x += Math.cos(this.currentAngle) * speed;
            this.y += Math.sin(this.currentAngle) * speed;
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
        recalculateDirection();
    }

    private void recalculateDirection() {
        if (targetNode == null) return;

        double currentCenterX = this.x - laneOffsetX;
        double currentCenterY = this.y - laneOffsetY;

        double dx = targetNode.getPosition().getX() - currentCenterX;
        double dy = targetNode.getPosition().getY() - currentCenterY;

        // Khử sai số để tìm ra hướng chính
        if (Math.abs(dx) > Math.abs(dy)) {
            this.dirX = Math.signum(dx);
            this.dirY = 0.0;
        } else {
            this.dirX = 0.0;
            this.dirY = Math.signum(dy);
        }

        // 🛠️ DẠT SANG LÀN BÊN PHẢI
        double laneDistance = 40.0;
        this.laneOffsetX = -this.dirY * laneDistance;
        this.laneOffsetY = this.dirX * laneDistance;

        // THÊM LẠI 2 DÒNG NÀY ĐỂ ÉP XE VÀO ĐÚNG TỌA ĐỘ LÀN NGAY LẬP TỨC
        // Không có 2 dòng này là xe nó đi chéo ngay!
        this.x = currentCenterX + this.laneOffsetX;
        this.y = currentCenterY + this.laneOffsetY;
    }

    // Cảm biến check tới đích
    public boolean hasReachedTarget() {
        if (targetNode == null) return false;

        Vector2D realTarget = new Vector2D(
                targetNode.getPosition().getX() + laneOffsetX,
                targetNode.getPosition().getY() + laneOffsetY
        );

        return this.getPosition().distanceTo(realTarget) < 5.0;
    }

    // ========================================================
    // HỆ THỐNG GIAO TIẾP
    // ========================================================
    public void honkAt(Vehicle frontCar) { if (frontCar != null) frontCar.receiveHonk(); }
    public void receiveHonk() { this.isRequestedToYield = true; }
    public boolean needsToYield() { return isRequestedToYield; }
    public void resetYieldFlag() { this.isRequestedToYield = false; }
    public void changeLane() { System.out.println("Xe đang đánh lái chuyển làn..."); }

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

    // 🎨 GIỜ TRẢ VỀ GÓC THẬT CỦA VÔ LĂNG ĐỂ CANVAS VẼ XOAY
    public double getAngle() {
        return Math.toDegrees(this.currentAngle);
    }

    public String getType() {
        String className = this.getClass().getSimpleName().toUpperCase();
        if (className.equals("FIRETRUCK")) return "FIRE_TRUCK";
        return className;
    }
}