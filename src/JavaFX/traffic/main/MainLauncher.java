package traffic.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import traffic.components.SimulationCanvas;
import traffic.components.ControlPanel;

// =========================================================
// IMPORT HỆ THỐNG MAP VÀ QUẢN LÝ XE ĐỜI MỚI
// =========================================================
import Map.map.RoadGraph;
import Map.map.MapLoader;
import Map.engine.VehicleManager;
import Map.engine.VehicleSpawner;

public class MainLauncher extends Application {

    private final boolean[] isPaused = {false};

    // KHỞI TẠO BỘ TƯ LỆNH, BẢN ĐỒ VÀ LÒ ẤP XE Ở ĐÂY
    public static VehicleManager vehicleManager = new VehicleManager();
    public static RoadGraph roadGraph;
    public static VehicleSpawner spawner = new VehicleSpawner();

    @Override
    public void start(Stage primaryStage) {
        traffic.render.Renderer.loadSprites();

        // 1. NẠP BẢN ĐỒ TỪ NHÁNH MAP_TRAFFIC
        roadGraph = MapLoader.loadMap();

        SimulationCanvas canvas = new SimulationCanvas();
        ControlPanel controlPanel = new ControlPanel();

        controlPanel.getBtnZoomIn().setOnAction(e -> canvas.zoomIn());
        controlPanel.getBtnZoomOut().setOnAction(e -> canvas.zoomOut());

        controlPanel.getBtnPause().setOnAction(e -> {
            isPaused[0] = true;
            controlPanel.getBtnPause().setDisable(true);
            controlPanel.getBtnResume().setDisable(false);
        });

        controlPanel.getBtnResume().setOnAction(e -> {
            isPaused[0] = false;
            controlPanel.getBtnPause().setDisable(false);
            controlPanel.getBtnResume().setDisable(true);
        });

        // TẠM KHÓA NÚT ADD ROAD VÌ HỆ THỐNG MỚI DÙNG MAPLOADER RỒI
        controlPanel.getBtnAddRoad().setOnAction(e -> {
            System.out.println("⚠️ Tính năng vẽ đường tự do đang được nâng cấp! Hiện tại bản đồ đã được nạp tự động qua MapLoader.");
        });

        // ==============================================================
        // NÚT SPAWN SIÊU GỌN NHẸ: GIAO VIỆC CHO VEHICLE SPAWNER!
        // ==============================================================
        controlPanel.getBtnSpawn().setOnAction(e -> {
            int count = controlPanel.getSpinnerSpawnCount().getValue();

            // Gọi 1 dòng duy nhất là máy ấp tự động nhả xe xịn ra đường
            spawner.spawnTraffic(vehicleManager, roadGraph, count);

            canvas.draw();
        });

        // (Giữ nguyên các nút chuyển Rectangle/Image)
        controlPanel.getBtnRectangle().setOnAction(e -> {
            canvas.setRectangleMode(true);
            controlPanel.getBtnRectangle().setStyle("-fx-background-color: #D3D3D3; -fx-border-color: #999999; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11; -fx-font-weight: bold;");
            controlPanel.getBtnImage().setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;");
        });

        controlPanel.getBtnImage().setOnAction(e -> {
            canvas.setRectangleMode(false);
            controlPanel.getBtnImage().setStyle("-fx-background-color: #D3D3D3; -fx-border-color: #999999; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11; -fx-font-weight: bold;");
            controlPanel.getBtnRectangle().setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #CCCCCC; -fx-border-radius: 2; -fx-background-radius: 2; -fx-font-family: 'Segoe UI'; -fx-font-size: 11;");
        });

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setLeft(controlPanel);

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("Traffic Simulation - AI Routing Enabled");
        primaryStage.setScene(scene);
        primaryStage.show();

        canvas.draw();

        // ==============================================================
        // GAME LOOP: CHẠY HỆ THỐNG AI MỚI
        // ==============================================================
        // ==============================================================
        // GAME LOOP: CHẠY HỆ THỐNG AI MỚI
        // ==============================================================
        new Thread(() -> {
            while (true) {
                if (!isPaused[0]) {
                    // Cấp đèn XANH vĩnh viễn cho xe chạy test Map trước đã
                    String currentLight = "GREEN";

                    // GỌI 1 LỆNH DUY NHẤT LÀ TOÀN BỘ XE TỰ TÍNH TOÁN RẼ, PHANH, CHẠY!
                    vehicleManager.updateAllMovement(currentLight);

                    Platform.runLater(() -> canvas.draw());
                }

                try { Thread.sleep(16); } catch (InterruptedException ex) { ex.printStackTrace(); }
            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}