package Map.engine; // (Giữ nguyên package của ông)

import java.util.ArrayList;
import java.util.List;
import VehicleSystem.vehicle.Vehicle;

public class VehicleManager {
    private List<Vehicle> vehicles;

    public VehicleManager() {
        vehicles = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    // =========================================================
    // HÀM MỚI: Truyền màu đèn giao thông và danh sách xe vào
    // để AI tự phân tích tình huống (Thay cho hàm move() cũ)
    // =========================================================
    public void updateAllMovement(String currentLightColor) {
        for (Vehicle vehicle : vehicles) {
            // Nhét toàn bộ danh sách xe (để dò va chạm) và màu đèn vào cho xe tự lái
            vehicle.update(vehicles, currentLightColor);
        }
    }

    // Nếu các file cũ của ông vẫn đang cố gọi hàm updateVehicles(),
    // tôi giữ lại hàm này làm phương án dự phòng (mặc định cho đèn Xanh)
    public void updateVehicles() {
        updateAllMovement("GREEN");
    }

    public List<Vehicle> getVehicles() {
        return vehicles;
    }
}