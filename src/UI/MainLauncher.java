package UI;

import MapSystem.map.MapLoader;
import MapSystem.map.RoadGraph;
import MapSystem.light.TrafficController;
import VehicleSystem.vehicle.VehicleManager;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainLauncher extends Application {

    // 1. KHAI BÁO CÁC CORE THÀNH PHẦN (Cơ sở dữ liệu tĩnh & Bộ máy quản lý)
    public static RoadGraph roadGraph;
    public static VehicleManager vehicleManager;
    public static TrafficController trafficController;

    // 2. KHAI BÁO THÀNH PHẦN GIAO DIỆN (UI)
    private SimulationCanvas simulationCanvas;
    private ControlPanel controlPanel;

    // Trạng thái tạm dừng của ứng dụng
    public static boolean isPaused = false;

    @Override
    public void start(Stage primaryStage) {
        try {
            // BƯỚC 1: KHỞI TẠO BỘ MÁY (ENGINE) & MAP TĨNH
            // Gọi thợ xây tạo map tĩnh 3x3 nạp vào bộ nhớ đồ thị
            roadGraph = MapLoader.loadMap();

            // Khởi tạo các bộ quản lý xe và đèn tín hiệu
            vehicleManager = new VehicleManager();
            trafficController = new TrafficController();

            // BƯỚC 2: KHỞI TẠO GIAO DIỆN (UI PARTS)
            simulationCanvas = new SimulationCanvas();
            controlPanel = new ControlPanel();

            // BƯỚC 3: LẮP RÁP BỐ CỤC MÀN HÌNH (LAYOUT)
            BorderPane root = new BorderPane();
            root.setLeft(controlPanel);       // Nút bấm, thanh công cụ nằm bên trái
            root.setCenter(simulationCanvas); // Sa bàn bản đồ nằm chính giữa làm trung tâm

            // Tạo Scene (Cửa sổ chứa Layout), kích thước tùy ông chỉnh
            Scene scene = new Scene(root, 1280, 720);

            primaryStage.setTitle("Traffic Simulation - Clean Architecture Framework");
            primaryStage.setScene(scene);

            // BƯỚC 4: KÍCH HOẠT NHỊP TIM HỆ THỐNG (GAME LOOP)
            AnimationTimer gameLoop = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    // Cứ 1/60 giây, hàm này sẽ bị gõ đầu gọi chạy một lần!

                    // Nhịp 1: Cập nhật Logic chạy ngầm (Chỉ chạy khi không bấm Tạm Dừng)
                    if (!isPaused) {
                        // Ép tất cả các xe tính toán vị trí, check va chạm, bẻ lái
                        vehicleManager.updateAllMovement();

                        // Ép bộ đếm thời gian đèn giao thông đếm lùi
                        trafficController.updateLights();
                    }

                    // Nhịp 2: Gọi họa sĩ vẽ lại giao diện dựa trên dữ liệu mới nhất
                    simulationCanvas.draw();
                }
            };

            // Bấm nút đề nổ Động cơ vòng lặp!
            gameLoop.start();

            // Hiển thị cửa sổ lên màn hình máy tính
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Lệnh kích hoạt JavaFX khởi chạy hàm start() ở trên
        launch(args);
    }
}