package VehicleSystem.vehicle;

import MapSystem.map.Intersection;
import MapSystem.map.RoadGraph;
import VehicleSystem.vehicle.type.*;
import VehicleSystem.behavior.*;

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
        Iterator<Vehicle> iterator = activeVehicles.iterator();

        while (iterator.hasNext()) {
            Vehicle v = iterator.next();
            v.update(activeVehicles);

            if (v.hasReachedTarget()) {
                assignNextTarget(v);
            }
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
            case "Motorbike": newVehicle = new Motorbike(sx, sy, 20, 10, 5.0, new NormalBehavior()); break;
            case "Ambulance": newVehicle = new Ambulance(sx, sy, 40, 20, 3.0, new EmergencyBehavior()); break;
            case "Fire Truck": newVehicle = new FireTruck(sx, sy, 50, 25, 4.0, new EmergencyBehavior()); break;
            case "Bus":        newVehicle = new Bus(sx, sy, 60, 30, 3.5, new NormalBehavior()); break;
            default:           newVehicle = new Car(sx, sy, 40, 20, 5.0, new NormalBehavior()); break;
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