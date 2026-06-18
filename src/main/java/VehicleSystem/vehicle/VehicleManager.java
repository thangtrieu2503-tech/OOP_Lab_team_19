package VehicleSystem.vehicle;

import MapSystem.map.Intersection;
import MapSystem.map.RoadGraph;
import VehicleSystem.behavior.*;
import VehicleSystem.vehicle.type.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class VehicleManager {
    // Thống nhất dùng 1 list này thôi
    private List<Vehicle> activeVehicles = new ArrayList<>();
    private RoadGraph map;
    private Random random;

    public VehicleManager(RoadGraph map) {
        this.map = map;
        this.random = new Random();
    }

    // ==========================================
    // VÒNG LẶP CẬP NHẬT (Giữ nguyên 100%)
    // ==========================================
    public void updateAll() {
        // 💀 THẦN CHẾT DỌN DẸP: Quét bay màu những xe bị dán bùa isDead (quá 5s)
        activeVehicles.removeIf(v -> v.isDead);

        Iterator<Vehicle> iterator = activeVehicles.iterator();

        // 🚨 1. THÊM CỜ KIỂM TRA XE CỨU THƯƠNG
        boolean hasEmergencyVehicle = false;

        while (iterator.hasNext()) {
            Vehicle v = iterator.next();
            v.update(activeVehicles);

            // 🚨 2. QUÉT XEM CÓ XE ƯU TIÊN TRÊN BẢN ĐỒ KHÔNG
            if (v.getType().equals("AMBULANCE") || v.getType().equals("FIRE_TRUCK")) {
                hasEmergencyVehicle = true;
            }

            if (v.hasReachedTarget()) {
                assignNextTarget(v);
            }
        }

        // 🚨 3. BẬT / TẮT CÒI TỰ ĐỘNG SAU KHI QUÉT XONG
        if (hasEmergencyVehicle) {
            UI.SoundManager.playSiren();
        } else {
            UI.SoundManager.stopSiren();
        }
    }

    // ==========================================
    // THUẬT TOÁN ĐIỀU HƯỚNG (ĐÃ CẬP NHẬT LUẬT LÀN ĐƯỜNG)
    // ==========================================
    public void assignNextTarget(Vehicle v) {
        Intersection currentIntersection = v.getTargetNode();
        if (currentIntersection == null) return;

        List<Intersection> neighbors = map.getNeighbors(currentIntersection);
        Intersection previousNode = v.getPreviousNode();

        // 1. Lọc hướng đi cơ bản (Chặn quay đầu)
        List<Intersection> validOptions = new ArrayList<>();
        for (Intersection n : neighbors) {
            if (!n.equals(previousNode)) {
                validOptions.add(n);
            }
        }

        // 2. Lọc hướng đi theo LÀN ĐƯỜNG
        List<Intersection> laneFilteredOptions = new ArrayList<>();
        int currentLane = v.getCurrentLane();

        for (Intersection nextNode : validOptions) {
            if (previousNode == null) {
                laneFilteredOptions.add(nextNode);
                continue;
            }

            double abX = currentIntersection.getPosition().getX() - previousNode.getPosition().getX();
            double abY = currentIntersection.getPosition().getY() - previousNode.getPosition().getY();

            double bcX = nextNode.getPosition().getX() - currentIntersection.getPosition().getX();
            double bcY = nextNode.getPosition().getY() - currentIntersection.getPosition().getY();

            // Tính tích có hướng để xét hướng rẽ
            double crossProduct = (abX * bcY) - (abY * bcX);
            boolean isLeftTurn = crossProduct < -10.0;
            boolean isRightTurn = crossProduct > 10.0;

            boolean isAllowed = true;

            // CẤM rẽ trái nếu đang ở làn 2 (chỉ được rẽ trái ở làn 0 và 1)
            if (isLeftTurn && currentLane == 2) isAllowed = false;

            // CẤM rẽ phải nếu đang ở làn 0 (chỉ được rẽ phải ở làn 1 và 2)
            if (isRightTurn && currentLane == 0) isAllowed = false;

            if (isAllowed) {
                laneFilteredOptions.add(nextNode);
            }
        }

        // 3. Xử lý kẹt luật: Nếu danh sách rỗng (vào ngõ cụt), bỏ luật để xe đi tiếp
        List<Intersection> finalOptions = laneFilteredOptions.isEmpty() ? validOptions : laneFilteredOptions;

        // 4. Chốt hướng
        if (!finalOptions.isEmpty()) {
            v.setPreviousNode(currentIntersection);
            v.setTargetNode(finalOptions.get(random.nextInt(finalOptions.size())));
        }
    }

    // ==========================================
    // LỆNH THẢ XE (SPAWN) (Giữ nguyên 100%)
    // ==========================================
    public void spawnVehicle(String type) {
        // Tìm tọa độ Node 1_0
        Intersection startNode = null;
        for (Intersection n : map.getIntersections()) {
            if ("Node_1_0".equals(n.getId())) {
                startNode = n;
                break;
            }
        }

        if (startNode == null) return;
        // Độ lệch tốc độ nhỏ để xe không chạy bằng khít nhau sau này
        double speedVariance = (random.nextDouble() * 1.0) - 0.5;

        if ("All".equals(type)) {
            String[] allTypes = {"Car", "Motorbike", "Ambulance", "Fire Truck", "Bus"};
            // Chọn ngẫu nhiên 1 vị trí từ 0 đến 4 trong mảng
            type = allTypes[random.nextInt(allTypes.length)];
        }

        Vehicle newVehicle = null;
        double sx = startNode.getPosition().getX();
        double sy = startNode.getPosition().getY();

        switch (type) {
            case "Motorbike":
                // Motorbike lúc nào sinh ra cũng được gán ngẫu nhiên 1 trong 2 tính cách
                newVehicle = new Motorbike(sx, sy, 7, 18, 0.5, new NormalBehavior());
                break;
            case "Ambulance":
                newVehicle = new Ambulance(sx, sy, 16, 38, 0.7, new EmergencyBehavior());
                break;
            case "Fire Truck":
                newVehicle = new FireTruck(sx, sy, 18, 40, 0.7, new EmergencyBehavior());
                break;
            case "Bus":
                // Bus to xác nên cho ngoan ngoãn thôi, không nên trẻ trâu
                newVehicle = new Bus(sx, sy, 18, 48, 0.7, random.nextBoolean() ? new AggressiveBehavior() : new NormalBehavior());
                break;
            default: // Car
                // Car cũng gán ngẫu nhiên 1 trong 2 tính cách
                newVehicle = new Car(sx, sy, 15, 35, 0.7, random.nextBoolean() ? new AggressiveBehavior() : new NormalBehavior());
                break;
        }

        if (newVehicle != null) {
            // 🛠️ MẸO CHIA LÀN: Lấy số lượng xe hiện tại chia lấy dư cho 6
            // Xe 1 -> làn 0, Xe 2 -> làn 1, ... Xe 6 -> làn 5, Xe 7 quay lại làn 0.
            int assignedLane = activeVehicles.size() % 3;

            newVehicle.setTargetNode(startNode);
            newVehicle.changeLane(assignedLane); // Ép xe vào làn được chỉ định luôn

            activeVehicles.add(newVehicle);
        }
    }

    // Getter chuẩn để Canvas gọi vào vẽ
    public List<Vehicle> getVehicles() {
        return activeVehicles;
    }
}