package Map.engine; // Giữ nguyên package của ông

// Nhớ import các class AI xịn mà mình vừa sửa nhé
import Map.engine.VehicleManager;
import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.vehicle.Type.Car;
import VehicleSystem.vehicle.Type.Bus;
import VehicleSystem.vehicle.Type.Ambulance;
import VehicleSystem.vehicle.Type.FireTruck;
import VehicleSystem.vehicle.Type.Motorbike;

import java.util.List;
import java.util.Random;

public class VehicleSpawner {
    private Random random;

    public VehicleSpawner() {
        random = new Random();
    }

    // =======================================================
    // LÒ ẤP XE RANDOM: Trả về một chiếc Vehicle bất kỳ
    // Bắt buộc phải truyền RoadGraph vào để xe biết đường chạy
    // =======================================================
    public Vehicle spawnRandomVehicle(RoadGraph roadGraph) {
        List<Intersection> nodes = roadGraph.getIntersections();

        // Check map rỗng
        if (nodes == null || nodes.isEmpty()) {
            System.out.println("⚠️ Lỗi: Bản đồ RoadGraph chưa có ngã tư, không thể đẻ xe!");
            return null;
        }

        // 1. Chọn ngẫu nhiên ngã tư xuất phát
        Intersection start = nodes.get(random.nextInt(nodes.size()));

        // 2. Tìm hàng xóm làm điểm đến đầu tiên (Ép chạy dọc đường nhựa)
        List<Intersection> neighbors = roadGraph.getNeighbors(start);
        Intersection target;
        if (neighbors != null && !neighbors.isEmpty()) {
            target = neighbors.get(random.nextInt(neighbors.size()));
        } else {
            target = start; // Ngõ cụt
        }

        // 3. Xóc đĩa chọn loại xe
        String[] vehicleTypes = {"CAR", "BUS", "AMBULANCE", "FIRE_TRUCK", "MOTORBIKE"};
        String type = vehicleTypes[random.nextInt(vehicleTypes.length)];

        // Tạo ID duy nhất cho xe
        String id = type + "_" + System.currentTimeMillis() + "_" + random.nextInt(1000);

        System.out.println("🚗 Đã xuất xưởng: " + type + " tại tọa độ (" + start.getPosition().getX() + ", " + start.getPosition().getY() + ")");

        // 4. Nhét các thông số vào xe và trả hàng
        switch (type) {
            case "BUS": return new Bus(id, start, target, roadGraph);
            case "AMBULANCE": return new Ambulance(id, start, target, roadGraph);
            case "FIRE_TRUCK": return new FireTruck(id, start, target, roadGraph);
            case "MOTORBIKE": return new Motorbike(id, start, target, roadGraph);
            case "CAR":
            default:
                return new Car(id, start, target, roadGraph);
        }
    }

    // =======================================================
    // HÀM ĐẺ HÀNG LOẠT: Ném 1 đống xe vào VehicleManager
    // =======================================================
    public void spawnTraffic(VehicleManager manager, RoadGraph roadGraph, int amount) {
        for (int i = 0; i < amount; i++) {
            Vehicle newVehicle = spawnRandomVehicle(roadGraph);
            if (newVehicle != null) {
                manager.addVehicle(newVehicle);
            }
        }
        System.out.println("✅ Đã thả xong " + amount + " xe lên đường!");
    }
}