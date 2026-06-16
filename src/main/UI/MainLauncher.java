package UI; 

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import MapSystem.map.MapLoader;
import MapSystem.map.RoadGraph;
import VehicleSystem.vehicle.VehicleManager;
// Nhớ import ControlPanel nếu file đó đang nằm ở traffic.components
// import traffic.components.ControlPanel;

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
        // 3. GAME LOOP - TRÁI TIM ĐỒ HỌA
        // =======================================================
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                vehicleManager.updateAll(); // Cập nhật logic vật lý
                canvas.render();            // Họa sĩ vẽ khung hình mới
            }
        };

        // =======================================================
        // 4. BẮT SỰ KIỆN NÚT BẤM TỪ CONTROL PANEL
        // =======================================================

        // --- Nút Spawn Xe ---
        controlPanel.getBtnSpawn().setOnAction(e -> {
            // Lấy dữ liệu từ giao diện của ông
            String selectedType = controlPanel.getComboVehicleType().getValue();
            int spawnCount = controlPanel.getSpinnerSpawnCount().getValue();

            // Nhả xe theo đúng số lượng ông chỉnh trong Spinner
            for (int i = 0; i < spawnCount; i++) {
                // Tạm thời gọi spawn mặc định. Sau này sẽ nâng cấp truyền selectedType vào đây!
                vehicleManager.spawnVehicle(selectedType);
            }
        });

        // --- Nút Pause (Tạm dừng mô phỏng) ---
        controlPanel.getBtnPause().setOnAction(e -> {
            gameLoop.stop(); // Đóng băng thời gian
            controlPanel.getBtnPause().setDisable(true);   // Khóa nút Pause
            controlPanel.getBtnResume().setDisable(false); // Mở nút Resume
        });

        // --- Nút Resume (Chạy tiếp) ---
        controlPanel.getBtnResume().setOnAction(e -> {
            gameLoop.start(); // Chạy lại thời gian
            controlPanel.getBtnResume().setDisable(true);  // Khóa nút Resume
            controlPanel.getBtnPause().setDisable(false);  // Mở lại nút Pause
        });

        // (Các nút Add Road, Remove, Zoom... giữ nguyên, lát code logic sau)

        // =======================================================
        // ĐẤU NỐI SỰ KIỆN ZOOM TỪ CONTROL PANEL SANG CANVAS
        // =======================================================
        
        // Bấm nút Zoom In trên thanh Sidebar -> Gọi canvas phóng to
        controlPanel.getBtnZoomIn().setOnAction(e -> {
            canvas.zoomIn();
        });

        // Bấm nút Zoom Out trên thanh Sidebar -> Gọi canvas thu nhỏ
        controlPanel.getBtnZoomOut().setOnAction(e -> {
            canvas.zoomOut();
        });

        // ĐẤU NỐI SỰ KIỆN CHUYỂN CHẾ ĐỘ ĐỒ HỌA XE CỘ
        // Khi bấm nút "Rectangle Mode" -> Gọi canvas bật hình hộp (true)
        controlPanel.getBtnRectangle().setOnAction(e -> {
            canvas.setRectangleMode(true);
        });

        // Khi bấm nút "Image Mode" -> Gọi canvas bật ảnh thật (false)
        controlPanel.getBtnImage().setOnAction(e -> {
            canvas.setRectangleMode(false);
        });

        // =======================================================
        // 5. HIỂN THỊ CỬA SỔ
        // =======================================================
        Scene scene = new Scene(root, 1280, 800);
        primaryStage.setTitle("Mô Phỏng Giao Thông - Hệ Thống Map & Làn Xe Đỉnh Cao");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false); // Khóa form, chống kéo giãn làm vỡ layout
        primaryStage.show();

        // Bóp cò khởi động vòng lặp!
        gameLoop.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}