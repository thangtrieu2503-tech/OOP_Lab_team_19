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

    // --- THÊM BIẾN CHUYỂN LÀN MƯỢT ---
    protected double targetLaneOffsetX = 0;
    protected double targetLaneOffsetY = 0;
    protected double laneChangeSpeed = 0.8; // Tốc độ trượt sang làn mới (càng to trượt càng nhanh)

    // --- THÊM BIẾN ĐỂ BO CUA TỪ XA ---
    protected double smoothDirX = 0;
    protected double smoothDirY = 0;
    protected double corneringSpeed = 0.15; // Tốc độ xoay vô lăng (Càng nhỏ ôm cua càng rộng)

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
    // BIẾN PHỤC VỤ XÓA XE KẸT (Đếm 5s)
    // ========================================================
    public long stuckTime = 0;
    public boolean isDead = false;

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
    // LÕI VẬT LÝ CƠ BẢN + LOGIC ĐI CHÉO CỰC CHUẨN (Point-to-Point)
    // ========================================================
    private void updatePhysics() {
        speed += acceleration;
        if (speed <= 0) { speed = 0; if (acceleration < 0) acceleration = 0; }
        if (speed > maxSpeed) speed = maxSpeed;

        if (speed > 0 && targetNode != null) {
            double moveX = 0;
            double moveY = 0;

            // Tốc độ đánh lái chéo (Bằng speed thì góc là 45 độ, nhân 0.8 thì góc thoai thoải đẹp hơn)
            double lateralSpeed = speed * 0.8;

            if (Math.abs(this.dirX) > 0) {
                // --- ĐANG TRÊN ĐƯỜNG NGANG (Trục X) ---
                moveX = this.dirX * speed;

                // Tọa độ Y lý tưởng của làn đường (Đường Ray)
                double idealY = targetNode.getPosition().getY() + this.targetLaneOffsetY;
                double distToIdealY = idealY - this.y;

                // Nếu đang lệch khỏi làn -> Đi chéo để nhập làn
                if (Math.abs(distToIdealY) > lateralSpeed) {
                    moveY = Math.signum(distToIdealY) * lateralSpeed;
                } else {
                    moveY = distToIdealY; // Khớp chuẩn xác vào vạch (Không bị run xe)
                }
            } else if (Math.abs(this.dirY) > 0) {
                // --- ĐANG TRÊN ĐƯỜNG DỌC (Trục Y) ---
                moveY = this.dirY * speed;

                // Tọa độ X lý tưởng của làn đường
                double idealX = targetNode.getPosition().getX() + this.targetLaneOffsetX;
                double distToIdealX = idealX - this.x;

                if (Math.abs(distToIdealX) > lateralSpeed) {
                    moveX = Math.signum(distToIdealX) * lateralSpeed;
                } else {
                    moveX = distToIdealX; // Khớp chuẩn xác vào vạch
                }
            }

            // Di chuyển xe
            this.x += moveX;
            this.y += moveY;

            // Chốt góc quay lập tức: Đầu xe luôn nhìn chính xác theo hướng vừa dịch chuyển
            if (moveX != 0 || moveY != 0) {
                this.currentAngle = Math.atan2(moveY, moveX);
            }
        }
    }

    // ========================================================
    // HỆ THỐNG ĐIỀU HƯỚNG VECTOR
    // ========================================================
    public void setTargetNode(Intersection targetNode) {
        this.targetNode = targetNode;

        // --- LOGIC RANDOM LÀN MỚI KHI RẼ ---
        this.currentLane = new java.util.Random().nextInt(laneDistances.length);

        recalculateDirection();
    }

    public void setLaneOffset(double ox, double oy) {
        this.laneOffsetX = ox;
        this.laneOffsetY = oy;
        recalculateDirection();
    }

    private void recalculateDirection() {
        if (targetNode == null) return;

        // Xác định hướng đường tổng quát cực kỳ dứt khoát
        double dx = targetNode.getPosition().getX() - this.x;
        double dy = targetNode.getPosition().getY() - this.y;

        if (Math.abs(dx) > Math.abs(dy)) {
            this.dirX = Math.signum(dx);
            this.dirY = 0.0;
        } else {
            this.dirX = 0.0;
            this.dirY = Math.signum(dy);
        }

        updateLaneOffset();
    }

    private void updateLaneOffset() {
        double laneDistance = laneDistances[this.currentLane];
        // Thay vì ép tọa độ thực, ta chỉ set tọa độ mục tiêu
        this.targetLaneOffsetX = -this.dirY * laneDistance;
        this.targetLaneOffsetY = this.dirX * laneDistance;

        // Nếu xe mới sinh ra (chưa có offset), cho phép nhảy thẳng vào làn luôn
        if (this.laneOffsetX == 0 && this.laneOffsetY == 0) {
            this.laneOffsetX = this.targetLaneOffsetX;
            this.laneOffsetY = this.targetLaneOffsetY;
        }
    }

    public boolean hasReachedTarget() {
        if (targetNode == null) return false;

        // Neo vào đúng tọa độ của làn tiếp theo
        Vector2D realTarget = new Vector2D(
                targetNode.getPosition().getX() + this.targetLaneOffsetX,
                targetNode.getPosition().getY() + this.targetLaneOffsetY
        );

        // Chạm mốc 90 mét là bắt đầu vạch đường chéo qua ngã tư
        return this.getPosition().distanceTo(realTarget) < 90.0;
    }

    // ========================================================
    // HỆ THỐNG GIAO TIẾP & CHUYỂN LÀN
    // ========================================================
    public void receiveHonk() { this.isRequestedToYield = true; }
    public boolean isRequestedToYield() { return this.isRequestedToYield; }
    public void setRequestedToYield(boolean requestedToYield) { this.isRequestedToYield = requestedToYield; }

    public void changeLane(int targetLane) {
        if (targetLane >= 0 && targetLane < laneDistances.length) {
            // --- KHÓA VÔ LĂNG CHỐNG LẮC ---
            double dx = this.targetLaneOffsetX - this.laneOffsetX;
            double dy = this.targetLaneOffsetY - this.laneOffsetY;
            if (Math.sqrt(dx * dx + dy * dy) > 1.0) {
                return; // Đang bận chuyển làn, từ chối lệnh mới!
            }

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