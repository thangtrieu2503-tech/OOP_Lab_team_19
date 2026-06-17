package MapSystem.light;

import java.util.List;
import java.util.ArrayList;

public class TrafficController {
    private List<TrafficLight> lights;
    private boolean isAutoMode;

    // Quản lý trạng thái pha thông minh
    private double phaseTimer;
    private double greenPhaseDuration = 8.0; // Thời gian đèn xanh chuẩn
    private double yellowPhaseDuration = 2.0;  // Thời gian đèn vàng chuẩn
    private boolean isInYellowPhase;
    private int currentPhaseIndex;

    public TrafficController() {
        this.lights = new ArrayList<>();
        this.isAutoMode = true;
        this.phaseTimer = greenPhaseDuration;
        this.isInYellowPhase = false;
        this.currentPhaseIndex = 0;
    }

    public void update(double deltaTime) {
        // 1. Cập nhật thời gian UI cho các đèn
        for (TrafficLight light : lights) {
            light.update(deltaTime);
        }

        if (!isAutoMode) return;
        if (lights.isEmpty()) return;

        // 2. Controller đếm ngược tổng
        phaseTimer -= deltaTime;

        if (phaseTimer <= 0) {
            if (!isInYellowPhase) {
                // HẾT PHA XANH -> CHUYỂN SANG VÀNG
                isInYellowPhase = true;
                phaseTimer = yellowPhaseDuration;

                for (int i = 0; i < lights.size(); i++) {
                    TrafficLight light = lights.get(i);
                    if (i == currentPhaseIndex && light.getCurrentState() == LightState.GREEN) {
                        // SỬA LỖI TẠI ĐÂY: Ép thông số chuẩn trước khi chuyển trạng thái
                        light.yellowDuration = this.yellowPhaseDuration;
                        light.setCurrentState(LightState.YELLOW);
                    }
                }
            } else {
                // HẾT PHA VÀNG -> CHUYỂN SANG XANH MỚI
                isInYellowPhase = false;
                phaseTimer = greenPhaseDuration;

                currentPhaseIndex = (currentPhaseIndex + 1) % lights.size();

                for (int i = 0; i < lights.size(); i++) {
                    TrafficLight light = lights.get(i);

                    // SỬA LỖI TẠI ĐÂY: Bắt buộc gán lại 15-3-18 của ông đè lên số của Map Bảo
                    light.greenDuration = this.greenPhaseDuration;
                    light.yellowDuration = this.yellowPhaseDuration;
                    light.redDuration = this.greenPhaseDuration + this.yellowPhaseDuration; // Đỏ = 18s

                    if (i == currentPhaseIndex) {
                        light.setCurrentState(LightState.GREEN);
                    } else {
                        light.setCurrentState(LightState.RED);
                    }
                }
            }
        }
    }

    public void addTrafficLight(TrafficLight light) {
        if (light != null && !lights.contains(light)) {
            this.lights.add(light);

            // SỬA LỖI TẠI ĐÂY: Vừa ném đèn vào là ép chuẩn thời gian luôn, không cho Map chạy bậy
            light.greenDuration = this.greenPhaseDuration;
            light.yellowDuration = this.yellowPhaseDuration;
            light.redDuration = this.greenPhaseDuration + this.yellowPhaseDuration;

            if (lights.size() == 1) {
                light.setCurrentState(LightState.GREEN);
            } else {
                light.setCurrentState(LightState.RED);
            }
        }
    }

    public void removeTrafficLight(TrafficLight light) {
        this.lights.remove(light);
    }

    public void clearAllLights() {
        this.lights.clear();
        this.currentPhaseIndex = 0;
        this.isInYellowPhase = false;
        this.phaseTimer = greenPhaseDuration;
    }

    public List<TrafficLight> getLights() {
        return this.lights;
    }

    public boolean isAutoMode() {
        return isAutoMode;
    }

    public void setAutoMode(boolean autoMode) {
        this.isAutoMode = autoMode;
        if (autoMode) {
            this.phaseTimer = greenPhaseDuration;
            this.isInYellowPhase = false;
        }
    }

    public void handleLightClick(TrafficLight clickedLight) {
        this.isAutoMode = false;
        if (clickedLight != null) {
            clickedLight.forceSwitchState();
        }
    }
}