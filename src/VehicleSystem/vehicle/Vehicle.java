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

    // === CÁC BIẾN MỚI ĐỂ XỬ LÝ LÀN ĐƯỜNG VÀ BẺ CUA ===
    private double currentTargetX;
    private double currentTargetY;
    private boolean isTurning = false;
    private final double LANE_OFFSET = 35.0; // Khoảng cách từ tim đường lệch ra giữa làn phải

    public Vehicle(String id, String type, IntersectionNode startNode, IntersectionNode firstTarget, double baseSpeed, List<IntersectionNode> fullMap, DrivingStrategy driver) {
        this.id = id;
        this.type = type;
        this.lastNode = startNode;
        this.targetNode = firstTarget;
        this.baseSpeed = baseSpeed;
        this.fullMap = fullMap;
        this.driver = driver;

        // Tính toán lệch làn ngay từ lúc mới đẻ ra
        double[] offset = calculateLaneOffset(startNode, firstTarget);
        this.x = startNode.getWorldX() + offset[0];
        this.y = startNode.getWorldY() + offset[1];

        this.currentTargetX = firstTarget.getWorldX() + offset[0];
        this.currentTargetY = firstTarget.getWorldY() + offset[1];
        this.isTurning = false;
    }

    public void update(List<Vehicle> allVehicles, String currentLightColor) {
        if (driver != null) driver.drive(this, allVehicles, currentLightColor);
    }

    // ==========================================
    // THUẬT TOÁN ĐI LỆCH LÀN & BẺ CUA TRONG NGÃ TƯ
    // ==========================================
    public void moveForward(double currentSpeed) {
        double dx = currentTargetX - this.x;
        double dy = currentTargetY - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= currentSpeed) {
            this.x = currentTargetX;
            this.y = currentTargetY;

            if (isTurning) {
                // Đã lách qua bùng binh xong, thẳng tiến tới ngã tư tiếp theo
                double[] offset = calculateLaneOffset(lastNode, targetNode);
                currentTargetX = targetNode.getWorldX() + offset[0];
                currentTargetY = targetNode.getWorldY() + offset[1];
                isTurning = false;
            } else {
                // Vừa chạm vạch ngã tư -> Tìm đường rẽ và bật mode bẻ cua
                pickRandomTurn();
            }
        } else {
            this.x += (dx / distance) * currentSpeed;
            this.y += (dy / distance) * currentSpeed;
            this.angle = Math.toDegrees(Math.atan2(dy, dx));
        }
    }

    private void pickRandomTurn() {
        List<IntersectionNode> possibleTurns = new ArrayList<>();
        IntersectionNode current = targetNode;

        for (IntersectionNode node : fullMap) {
            int gridDx = Math.abs(node.getGridX() - current.getGridX());
            int gridDy = Math.abs(node.getGridY() - current.getGridY());

            if ((gridDx == 1 && gridDy == 0) || (gridDx == 0 && gridDy == 1)) {
                if (node != lastNode) possibleTurns.add(node); // Không quay đầu
            }
        }

        IntersectionNode nextNode;
        if (!possibleTurns.isEmpty()) {
            nextNode = possibleTurns.get(random.nextInt(possibleTurns.size()));
        } else {
            nextNode = lastNode; // Nếu ngõ cụt thì buộc quay đầu
        }

        lastNode = current;
        targetNode = nextNode;

        // ÉP XE BẺ LÁI TRONG GIAO LỘ ĐỂ CHUYỂN LÀN
        double[] newOffset = calculateLaneOffset(lastNode, targetNode);
        currentTargetX = lastNode.getWorldX() + newOffset[0];
        currentTargetY = lastNode.getWorldY() + newOffset[1];
        isTurning = true;
    }

    // ==========================================
    // LOGIC NHẬN DIỆN CHIỀU ĐI ĐỂ TẤP VÀO LỀ PHẢI
    // ==========================================
    private double[] calculateLaneOffset(IntersectionNode from, IntersectionNode to) {
        if (from.getGridY() < to.getGridY()) return new double[]{-LANE_OFFSET, 0.0}; // Đi xuống -> Tấp lề trái (màn hình)
        if (from.getGridY() > to.getGridY()) return new double[]{LANE_OFFSET, 0.0};  // Đi lên -> Tấp lề phải
        if (from.getGridX() < to.getGridX()) return new double[]{0.0, LANE_OFFSET};  // Sang phải -> Tấp lề dưới
        if (from.getGridX() > to.getGridX()) return new double[]{0.0, -LANE_OFFSET}; // Sang trái -> Tấp lề trên
        return new double[]{0.0, 0.0};
    }

    // ==========================================
    // CẢM BIẾN VÀ THÔNG SỐ VẬT LÝ
    // ==========================================
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

    public void brake() { /* Không cộng tọa độ, xe tự đứng im */ }

    public boolean isFinished() { return false; }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getAngle() { return angle; }
    public String getId() { return id; }
    public String getType() { return type; }
    public double getBaseSpeed() { return baseSpeed; }
    public boolean isTurning() { return isTurning; }
}