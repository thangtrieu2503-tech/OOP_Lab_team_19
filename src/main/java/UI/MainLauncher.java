package UI;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import MapSystem.map.MapLoader;
import MapSystem.map.RoadGraph;
import VehicleSystem.vehicle.VehicleManager;
import MapSystem.map.Intersection;

public class MainLauncher extends Application {

    private AnimationTimer gameLoop;

    @Override
    public void start(Stage primaryStage) {
        // =======================================================
        // 1. KHỞI TẠO DỮ LIỆU LÕI (Bắt đầu với bãi đất trống)
        // =======================================================
        RoadGraph map = new RoadGraph(); // 🛠️ Sửa thành Map trắng để tự vẽ
        VehicleManager vehicleManager = new VehicleManager(map);

        // =======================================================
        // 2. KHỞI TẠO GIAO DIỆN
        // =======================================================
        SimulationCanvas canvas = new SimulationCanvas(1090, 800, map, vehicleManager);
        ControlPanel controlPanel = new ControlPanel();

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(controlPanel);

        // =======================================================
        // 3. GAME LOOP - TRÁI TIM ĐỒ HỌA
        // =======================================================
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                if (deltaTime > 0.1) deltaTime = 0.1;

                // Cho xe chạy
                vehicleManager.updateAll();

                // Lắp pin cho hệ thống đèn
                for (Intersection node : map.getIntersections()) {
                    if (node.getTrafficController() != null) {
                        node.getTrafficController().update(deltaTime);
                    }
                }

                // Vẽ lại toàn bộ
                canvas.render();
            }
        };

        // =======================================================
        // 4. BẮT SỰ KIỆN NÚT BẤM TỪ CONTROL PANEL
        // =======================================================

        // 🛠️ --- CỤM SỰ KIỆN MAP BUILDER --- 🛠️
        controlPanel.getBtnAddNode().setOnAction(e -> {
            canvas.setEditMode(SimulationCanvas.EditMode.ADD_NODE);
        });

        controlPanel.getBtnAddRoad().setOnAction(e -> {
            canvas.setEditMode(SimulationCanvas.EditMode.ADD_ROAD);
        });

        controlPanel.getBtnRemoveNode().setOnAction(e -> {
            canvas.setEditMode(SimulationCanvas.EditMode.REMOVE_NODE);
        });

        controlPanel.getBtnLoadDefault().setOnAction(e -> {
            // Dọn sạch bãi đất và xe cộ cũ
            map.getIntersections().clear();
            map.getRoads().clear();
            vehicleManager.getVehicles().clear();

            // Kéo dữ liệu từ MapLoader vào map hiện tại
            RoadGraph defaultMap = MapLoader.loadMap();
            map.getIntersections().addAll(defaultMap.getIntersections());
            map.getRoads().addAll(defaultMap.getRoads());

            // Tắt công cụ vẽ, render lại màn hình
            canvas.setEditMode(SimulationCanvas.EditMode.NONE);
            System.out.println("✅ Đã tải Map mẫu 3x3 thành công!");
        });

        // --- Nút Spawn Xe ---
        controlPanel.getBtnSpawn().setOnAction(e -> {
            String selectedType = controlPanel.getComboVehicleType().getValue();
            int spawnCount = controlPanel.getSpinnerSpawnCount().getValue();

            for (int i = 0; i < spawnCount; i++) {
                vehicleManager.spawnVehicle(selectedType);
            }
        });

        // --- Nút Pause (Tạm dừng mô phỏng) ---
        controlPanel.getBtnPause().setOnAction(e -> {
            gameLoop.stop();
            controlPanel.getBtnPause().setDisable(true);
            controlPanel.getBtnResume().setDisable(false);
        });

        // --- Nút Resume (Chạy tiếp) ---
        controlPanel.getBtnResume().setOnAction(e -> {
            canvas.setEditMode(SimulationCanvas.EditMode.NONE); // 🛠️ Bấm chạy tiếp là tự cất công cụ vẽ đi
            gameLoop.start();
            controlPanel.getBtnResume().setDisable(true);
            controlPanel.getBtnPause().setDisable(false);
        });

        // --- Nút Camera ---
        controlPanel.getBtnZoomIn().setOnAction(e -> canvas.zoomIn());
        controlPanel.getBtnZoomOut().setOnAction(e -> canvas.zoomOut());

        // --- Nút Chế độ đồ họa ---
        controlPanel.getBtnRectangle().setOnAction(e -> canvas.setRectangleMode(true));
        controlPanel.getBtnImage().setOnAction(e -> canvas.setRectangleMode(false));

        // =======================================================
        // 5. HIỂN THỊ CỬA SỔ
        // =======================================================
        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setTitle("Traffic Simulation");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        // Bóp cò khởi động vòng lặp!
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}