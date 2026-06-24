package VehicleSystem.vehicle;

import MapSystem.map.Intersection;
import MapSystem.map.RoadGraph;
import VehicleSystem.behavior.EmergencyDriver;
import VehicleSystem.behavior.NormalDriver;
import VehicleSystem.vehicle.type.*;

import java.util.List;
import java.util.Random;

public class VehicleSpawner {
    private Random random;

    public VehicleSpawner() {
        this.random = new Random();
    }

    // Hàm chuyên trách đúc xe tại đúng nút 1,0
    public void spawnVehicleAtNode1_0(RoadGraph map, List<Vehicle> vehiclesList) {
        Intersection spawnPoint = null;

        // Tìm đúng ngã tư có ID "Node_1_0" từ MapLoader của ông
        for (Intersection node : map.getIntersections()) {
            if (node.getId().equals("Node_1_0")) {
                spawnPoint = node;
                break;
            }
        }

        // Nếu map chưa load xong hoặc sai tên ID thì chặn lỗi
        if (spawnPoint == null) {
            return;
        }

        double startX = spawnPoint.getPosition().getX();
        double startY = spawnPoint.getPosition().getY();

        // Tỷ lệ bốc ngẫu nhiên 1 trong 5 loại xe mới
        int vehicleType = random.nextInt(5);
        Vehicle newVehicle;

        switch (vehicleType) {
            case 0: newVehicle = new Ambulance(startX, startY, 40, 20, 6.0, new EmergencyDriver()); break;
            case 1: newVehicle = new Bus(startX, startY, 60, 30, 3.5, new NormalDriver()); break;
            case 2: newVehicle = new Car(startX, startY, 40, 20, 5.0, new NormalDriver()); break;
            case 3: newVehicle = new FireTruck(startX, startY, 50, 25, 4.0, new EmergencyDriver()); break;
            default: newVehicle = new Motorbike(startX, startY, 20, 10, 5.0, new NormalDriver()); break; // case 4
        }

        // Gán tạm mục tiêu ban đầu chính là nút nó đang đứng để không bị lỗi Vector hướng bằng 0
        newVehicle.setTargetNode(spawnPoint);

        // Thêm xe vào danh sách tổng của hệ thống
        vehiclesList.add(newVehicle);
    }
}