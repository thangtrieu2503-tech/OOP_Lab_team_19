package traffic;

import java.util.List;
import java.util.ArrayList;

public class TrafficController {
    private List<TrafficLight> lights;
    private boolean isAutoMode; // True: Tự động, False: Thủ công

    public TrafficController() {
        this.lights = new ArrayList<>();
        this.isAutoMode = true; // Mặc định là tự động
    }

    public void addTrafficLight(TrafficLight light) {
        lights.add(light);
    }

    // Hàm này sẽ được gọi liên tục bởi SimulationEngine/System Timer
    public void update() {
        if (isAutoMode) {
            for (TrafficLight light : lights) {
                light.update();
            }
        }
    }

    // Kích hoạt khi người dùng (User) click chuyển đèn thủ công
    public void manualSwitch() {
        if (!isAutoMode) {
            for (TrafficLight light : lights) {
                light.switchLight();
            }
        }
    }

    public void setAutoMode(boolean autoMode) {
        this.isAutoMode = autoMode;
    }

    public boolean isAutoMode() {
        return isAutoMode;
    }
}
