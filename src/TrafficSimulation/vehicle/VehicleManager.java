package TrafficSimulation.vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleManager {
    // Sổ xố lưu trữ toàn bộ xe đang lăn bánh trên sa bàn
    private List<Vehicle> activeVehicles;

    public VehicleManager() {
        this.activeVehicles = new ArrayList<>();
    }

    // ==========================================
    // 1. NẠP XE VÀO BẢN ĐỒ
    // ==========================================
    public void addVehicle(Vehicle v) {
        activeVehicles.add(v);
    }

    // ==========================================
    // 2. TRÁI TIM CỦA HỆ THỐNG (Gọi 60 khung hình/giây)
    // ==========================================
    public void updateAllMovement(String currentLightColor) {
        // Bước 1: Quét và dọn dẹp các xe đã đi hết lộ trình để giải phóng RAM
        removeFinishedVehicles();

        // Bước 2: Gọi tất cả các xe còn lại trên bản đồ ra chạy
        for (Vehicle v : activeVehicles) {
            // Đây chính là lúc cung cấp "Môi trường" cho dòng 35 của Vehicle hoạt động!
            // Ném danh sách tổng và màu đèn vào để AI xe tự né nhau và dừng đèn đỏ.
            v.update(activeVehicles, currentLightColor);
        }
    }

    // ==========================================
    // 3. DỌN DẸP RÁC (Garbage Collection)
    // ==========================================
    private void removeFinishedVehicles() {
        // Dùng biểu thức Lambda để xóa: Nếu hàm isFinished() trả về true -> Xóa ngay!
        activeVehicles.removeIf(Vehicle::isFinished);
    }

    // ==========================================
    // 4. GETTER CHO GIAO DIỆN VẼ
    // ==========================================
    // Hàm này để cho thằng SimulationCanvas lôi danh sách xe ra quét sơn lên màn hình
    public List<Vehicle> getActiveVehicles() {
        return activeVehicles;
    }
}