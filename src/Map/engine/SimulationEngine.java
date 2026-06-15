package Map.engine;

import Map.map.*;
import Map.trafficLight.*;
import VehicleSystem.vehicle.Vehicle;

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

        // Khởi tạo bản đồ xịn từ nhánh mới
        graph = MapLoader.loadMap();

        trafficController = new TrafficController();
        isRunning = false;
    }

    public void start() {
        isRunning = true;

        // Thêm đèn giao thông thử nghiệm vào Controller
        trafficController.addLight(new CountdownTrafficLight(LightState.RED, 15));

        // ==============================================================
        // SỬA LỖI ĐẺ XE: Dùng hàm spawn mới, truyền RoadGraph vào
        // ==============================================================
        Vehicle firstVehicle = spawner.spawnRandomVehicle(graph);
        if (firstVehicle != null) {
            vehicleManager.addVehicle(firstVehicle);
        }

        // (Nếu ông thích đẻ 1 phát 5 xe cho đông vui thì mở comment dòng dưới ra dùng)
        // spawner.spawnTraffic(vehicleManager, graph, 5);

        camera.zoomIn();

        System.out.println("--- BẮT ĐẦU MÔ PHỎNG (Bấm dừng chương trình theo cách thủ công) ---");

        // Vòng lặp mô phỏng (Simulation Loop) chạy liên tục
        int tick = 0;
        while (isRunning && tick < 20) { // Giới hạn tạm 20 nhịp để tránh treo Console
            System.out.println("\n--- [Nhịp thứ " + tick + "] ---");

            // 1. Cập nhật trạng thái đèn
            trafficController.updateLights();

            // 2. Cập nhật xe cộ di chuyển
            // Tạm thời gọi hàm updateVehicles() dự phòng (mặc định cho đèn GREEN).
            // (Nếu ông muốn xe dừng đèn đỏ thật, ông cần lấy màu đèn từ trafficController
            // và gọi: vehicleManager.updateAllMovement("RED" hoặc "GREEN"))
            vehicleManager.updateVehicles();

            tick++;
            try {
                Thread.sleep(500); // Dừng nửa giây giữa mỗi nhịp
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopSimulation() {
        this.isRunning = false;
    }

    // ==============================================================
    // THÊM CÁC HÀM GETTER ĐỂ MỞ CỬA CHO GIAO DIỆN (CANVAS) VÀO LẤY ĐỒ VẼ
    // ==============================================================
    public VehicleManager getVehicleManager() { return vehicleManager; }
    public RoadGraph getGraph() { return graph; }
    public Camera getCamera() { return camera; }
    public TrafficController getTrafficController() { return trafficController; }
}