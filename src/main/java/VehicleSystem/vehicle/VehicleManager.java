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
    // VÒNG LẶP CẬP NHẬT
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
    // THUẬT TOÁN ĐIỀU HƯỚNG
    // ==========================================
    public void assignNextTarget(Vehicle v) {
        Intersection currentIntersection = v.getTargetNode();
        if (currentIntersection == null) return;

        List<Intersection> neighbors = map.getNeighbors(currentIntersection);
        Intersection previousNode = v.getPreviousNode();

        List<Intersection> validOptions = new ArrayList<>();
        for (Intersection n : neighbors) {
            // Chặn quay đầu: không đi ngược lại ngã tư vừa xong
            if (!n.equals(previousNode)) {
                validOptions.add(n);
            }
        }

        if (!validOptions.isEmpty()) {
            v.setPreviousNode(currentIntersection);
            v.setTargetNode(validOptions.get(random.nextInt(validOptions.size())));
        }
    }

    // ==========================================
    // LỆNH THẢ XE (SPAWN)
    // ==========================================
    public void spawnVehicle(String type) {
        // 1. KIỂM TRA AN TOÀN: Nếu map chưa có ngã tư nào thì dừng lại, cấm đẻ xe
        if (map.getIntersections().isEmpty()) {
            System.out.println("⚠️ Bản đồ chưa có ngã tư nào! Hãy dùng công cụ Add Node để tạo điểm xuất phát trước.");
            return;
        }

        // 2. LẤY NODE ĐẦU TIÊN TẠO RA (Vị trí 0) LÀM ĐIỂM XUẤT PHÁT
        Intersection startNode = map.getIntersections().get(0);

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
                newVehicle = new Motorbike(sx, sy, 7, 18, 0.7, new NormalBehavior());
                break;
            case "Ambulance":
                newVehicle = new Ambulance(sx, sy, 16, 38, 1.0, new EmergencyBehavior());
                break;
            case "Fire Truck":
                newVehicle = new FireTruck(sx, sy, 18, 40, 1.0, new EmergencyBehavior());
                break;
            case "Bus":
                // Bus to xác nên cho ngoan ngoãn thôi, không nên trẻ trâu
                newVehicle = new Bus(sx, sy, 18, 48, 1.0, random.nextBoolean() ? new AggressiveBehavior() : new NormalBehavior());
                break;
            default: // Car
                // Car cũng gán ngẫu nhiên 1 trong 2 tính cách
                newVehicle = new Car(sx, sy, 15, 35, 1.0, random.nextBoolean() ? new AggressiveBehavior() : new NormalBehavior());
                break;
        }

        if (newVehicle != null) {
            // 🛠️ MẸO CHIA LÀN: Lấy số lượng xe hiện tại chia lấy dư cho 3
            // Xe 1 -> làn 0, Xe 2 -> làn 1, Xe 3 -> làn 2, Xe 4 quay lại làn 0...
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