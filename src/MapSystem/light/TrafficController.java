package MapSystem.light;

import java.util.List;
import java.util.ArrayList;

public class TrafficController {
    private List<TrafficLight> lights;
    private boolean isAutoMode;
    private double phaseTimer;
    private int currentPhaseIndex;
    private double phaseDuration = 15.0; // Thời gian cho mỗi pha đèn

    public TrafficController() {
        this.lights = new ArrayList<>();
        this.isAutoMode = true; // Mặc định chạy tự động
        this.phaseTimer = phaseDuration;
        this.currentPhaseIndex = 0;
    }

    // Nhận nhịp cập nhật từ SimulationEngine của Bảo
    public void update(double deltaTime) {
        if (!isAutoMode) return; // Nếu đang bật thủ công bằng tay thì đóng băng bộ đếm tự động

        // Cách 1: Cho các đèn tự đếm độc lập (Nếu cấu hình theo cụm đường)
        for (TrafficLight light : lights) {
            light.update(deltaTime);
        }

        // Cách 2: Logic phân pha cho ngã rẽ động (Thích ứng Ngã 3, 4, 5 của Bảo)
        /*
        if (lights.isEmpty()) return;
        phaseTimer -= deltaTime;
        if (phaseTimer <= 0) {
            phaseTimer = phaseDuration;
            // Thuật toán luân chuyển pha dựa trên số lượng đèn thực tế tại nút giao
            currentPhaseIndex = (currentPhaseIndex + 1) % lights.size();
            for (int i = 0; i < lights.size(); i++) {
                if (i == currentPhaseIndex) {
                    lights.get(i).setCurrentState(LightState.GREEN);
                } else {
                    lights.get(i).setCurrentState(LightState.RED);
                }
            }
        }
        */
    }

    // --- CÁC ĐẦU NỐI KẾT NỐI VỚI MAP ĐỘNG CỦA BẢO ---
    public void addTrafficLight(TrafficLight light) {
        if (light != null && !lights.contains(light)) {
            this.lights.add(light);
        }
    }

    public void removeTrafficLight(TrafficLight light) {
        this.lights.remove(light);
    }

    public void clearAllLights() {
        this.lights.clear();
        this.currentPhaseIndex = 0;
    }

    public List<TrafficLight> getLights() {
        return this.lights;
    }

    // --- CÁC HÀM TƯƠNG TÁC TỪ NGƯỜI DÙNG (CLICK CHUỘT TRÊN UI CỦA NHÂN) ---
    public boolean isAutoMode() {
        return isAutoMode;
    }

    public void setAutoMode(boolean autoMode) {
        this.isAutoMode = autoMode;
    }

    // Khi người dùng click chuột trực tiếp vào một cái đèn cụ thể trên GUI
    public void handleLightClick(TrafficLight clickedLight) {
        // Bước 1: Ép hệ thống chuyển sang chế độ Thủ công ngay lập tức để người dùng điều khiển
        this.isAutoMode = false;

        // Bước 2: Ép cái đèn được click chuyển màu
        if (clickedLight != null) {
            clickedLight.forceSwitchState();
        }
    }
}