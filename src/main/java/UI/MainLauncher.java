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
        // 1. KHỞI TẠO DỮ LIỆU LÕI (Mặt Trận Ngầm)
        // =======================================================
        RoadGraph map = MapLoader.loadMap();
        VehicleManager vehicleManager = new VehicleManager(map);

        // =======================================================
        // 2. KHỞI TẠO GIAO DIỆN (Mặt Trận Nổi)
        // =======================================================
        // Canvas rộng 1090px, chừa đúng 190px cho Sidebar của ông là khít rịt 1280px
        SimulationCanvas canvas = new SimulationCanvas(1090, 800, map, vehicleManager);
        ControlPanel controlPanel = new ControlPanel();

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setRight(controlPanel); // Ốp Sidebar sang lề phải

        // =======================================================
        // 3. GAME LOOP - TRÁI TIM ĐỒ HỌA (ĐÃ ĐƯỢC SỬA)
        // =======================================================
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0; // Biến ghi nhớ mốc thời gian của khung hình trước

            @Override
            public void handle(long now) {
                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                // Tính toán chính xác thời gian trôi qua thực tế giữa 2 khung hình (đơn vị: giây)
                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                // Chặn đứng các pha giật lag hệ thống bất ngờ khiến deltaTime bị vọt lên quá cao
                if (deltaTime > 0.1) deltaTime = 0.1;

                // a) Cho xe chạy (Code cũ của nhóm)
                vehicleManager.updateAll();

                // b) 🔥 DÒNG CODE THẦN THÁNH: Lắp pin cho hệ thống đèn của ông Thắng chạy 🔥
                for (Intersection node : map.getIntersections()) {
                    if (node.getTrafficController() != null) {
                        node.getTrafficController().update(deltaTime); // Đập nhịp thời gian thực vào bộ điều khiển pha
                    }
                }

                // c) Quét chổi sơn vẽ lại toàn bộ sa hình ra màn hình
                canvas.render();
            }
        };

        // =======================================================
        // 4. BẮT SỰ KIỆN NÚT BẤM TỪ CONTROL PANEL
        // =======================================================

        // 🚨 THÊM MỚI: Nút Chuyển chế độ đèn đếm ngược
        final boolean[] isCountdownMode = {true}; // Mặc định map đang dùng đèn có đếm số

        controlPanel.getBtnNoCountdownLightMode().setOnAction(e -> {
            isCountdownMode[0] = !isCountdownMode[0]; // Đảo ngược trạng thái

            if (isCountdownMode[0]) {
                controlPanel.getBtnNoCountdownLightMode().setText("Countdown: ON");
            } else {
                controlPanel.getBtnNoCountdownLightMode().setText("Countdown: OFF");
            }

            // Quét toàn bộ ngã tư trên bản đồ và truyền lệnh đổi bóng đèn xuống Controller
            for (Intersection node : map.getIntersections()) {
                if (node.getTrafficController() != null) {
                    // Truyền lệnh xuống TrafficController
                    node.getTrafficController().setCountdownMode(isCountdownMode[0]);
                }
            }
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
            gameLoop.stop(); // Đóng băng thời gian đồ họa

            // 🛑 THÊM DÒNG NÀY: Báo cho Manager biết là game đã dừng để nó tắt còi
            vehicleManager.setPaused(true);

            controlPanel.getBtnPause().setDisable(true);
            controlPanel.getBtnResume().setDisable(false);
        });

        // --- Nút Resume (Chạy tiếp) ---
        controlPanel.getBtnResume().setOnAction(e -> {

            // 🟢 THÊM DÒNG NÀY: Báo cho Manager biết game chạy lại để nó hú còi tiếp
            vehicleManager.setPaused(false);

            gameLoop.start(); // Chạy lại thời gian đồ họa
            controlPanel.getBtnResume().setDisable(true);
            controlPanel.getBtnPause().setDisable(false);
        });

        // 🚨 THÊM MỚI: Nút Mute (Tắt/Bật âm thanh thủ công)
        controlPanel.getBtnMute().setOnAction(e -> {
            boolean currentMute = VehicleManager.isMuted;
            vehicleManager.setMuted(!currentMute); // Đảo trạng thái

            if (VehicleManager.isMuted) {
                controlPanel.getBtnMute().setText("Mute Sound: ON");
            } else {
                controlPanel.getBtnMute().setText("Mute Sound: OFF");
            }
        });

        // Bấm nút Zoom In trên thanh Sidebar -> Gọi canvas phóng to
        controlPanel.getBtnZoomIn().setOnAction(e -> {
            canvas.zoomIn();
        });

        // Bấm nút Zoom Out trên thanh Sidebar -> Gọi canvas thu nhỏ
        controlPanel.getBtnZoomOut().setOnAction(e -> {
            canvas.zoomOut();
        });

        // ĐẤU NỐI SỰ KIỆN CHUYỂN CHẾ ĐỘ ĐỒ HỌA XE CỘ
        controlPanel.getBtnRectangle().setOnAction(e -> {
            canvas.setRectangleMode(true);
        });

        controlPanel.getBtnImage().setOnAction(e -> {
            canvas.setRectangleMode(false);
        });

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