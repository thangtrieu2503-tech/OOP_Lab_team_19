package engine;

import map.*;
import traffic.*;

public class SimulationEngine {
    private VehicleManager vehicleManager;
    private VehicleSpawner spawner;
    private Camera camera;
    private RoadGraph graph;
    private TrafficController trafficController;
    private boolean isRunning;

    public SimulationEngine() {
        vehicleManager = new VehicleManager();
        spawner = new VehicleSpawner();
        camera = new Camera();
        graph = MapLoader.loadMap();
        trafficController = new TrafficController();
        isRunning = false;
    }

    public void start() {
        isRunning = true;

        // Thêm đèn giao thông thử nghiệm vào Controller
        trafficController.addLight(new CountdownTrafficLight(LightState.RED, 15));

        // Sinh thử 1 xe ban đầu
        vehicleManager.addVehicle(spawner.spawnRandomCar());
        camera.zoomIn();

        System.out.println("--- BẮT ĐẦU MÔ PHỎNG (Bấm dừng chương trình theo cách thủ công) ---");

        // Vòng lặp mô phỏng (Simulation Loop) chạy liên tục
        int tick = 0;
        while (isRunning && tick < 20) { // Giới hạn tạm 20 nhịp để tránh treo Console khi chạy thử lần đầu
            System.out.println("\n--- [Nhịp thứ " + tick + "] ---");

            // 1. Cập nhật trạng thái đèn
            trafficController.updateLights();

            // 2. Cập nhật xe cộ di chuyển
            vehicleManager.updateVehicles();

            tick++;
            try {
                Thread.sleep(500); // Dừng nửa giây giữa mỗi nhịp cho dễ nhìn log
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopSimulation() {
        this.isRunning = false;
    }
}