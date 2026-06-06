package vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleSystem {
    private List<Vehicle> allVehicles;

    public VehicleSystem() {
        this.allVehicles = new ArrayList<>();
    }

    public void spawnVehicle(Vehicle vehicle) {
        this.allVehicles.add(vehicle);
    }

    public void updateSystem(boolean isRedLight, double trafficLightX, double trafficLightY) {
        for (Vehicle current : allVehicles) {

            // 1. Tìm xe đi ngay phía trước dựa trên khoảng cách hình học 2D
            Vehicle frontVehicle = findFrontVehicle(current);

            // 2. Khoảng cách tới đèn giao thông tính theo đường thẳng 2D
            double dx = trafficLightX - current.getX();
            double dy = trafficLightY - current.getY();
            double distanceToLight = Math.sqrt(dx * dx + dy * dy);

            // 3. Nạp dữ liệu vào AI
            current.applyAI(frontVehicle, isRedLight, distanceToLight, allVehicles);

            // 4. Di chuyển xe
            current.move();
        }

        // Tự động xóa xe khi nó đi hết danh sách điểm mốc đường đi
        allVehicles.removeIf(v -> v.currentWaypointIndex >= v.waypoints.size());
    }

    private Vehicle findFrontVehicle(Vehicle current) {
        Vehicle closestFront = null;
        double minDistance = Double.MAX_VALUE;

        for (Vehicle other : allVehicles) {
            if (other == current) continue;

            // Tính khoảng cách giữa 2 xe
            double dx = other.getX() - current.getX();
            double dy = other.getY() - current.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Nếu xe kia ở gần (dưới 120px) và có góc di chuyển tương đồng (đang đi cùng đường)
            if (distance < 120 && Math.abs(other.getAngle() - current.getAngle()) < 30) {
                if (distance < minDistance) {
                    minDistance = distance;
                    closestFront = other;
                }
            }
        }
        return closestFront;
    }

    public List<Vehicle> getAllVehicles() { return allVehicles; }
}