package traffic.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import traffic.components.TrafficEngine;
import traffic.components.SimulationCanvas;
import traffic.components.ControlPanel;
import traffic.map.IntersectionNode;

import VehicleSystem.vehicle.VehicleManager;
import VehicleSystem.vehicle.Type.Car;
import VehicleSystem.vehicle.Type.Ambulance;
import VehicleSystem.vehicle.Type.Motorbike;
import VehicleSystem.vehicle.Type.FireTruck;
import VehicleSystem.vehicle.Type.Bus;

import java.util.List;
import java.util.Random;

public class MainLauncher extends Application {

    private static int nextGridX = 3;
    private final boolean[] isPaused = {false};

    // KHỞI TẠO TỔNG TƯ LỆNH QUẢN LÝ XE Ở ĐÂY
    public static VehicleManager vehicleManager = new VehicleManager();

    @Override
    public void start(Stage primaryStage) {
        traffic.render.Renderer.loadSprites();
        TrafficEngine trafficEngine = new TrafficEngine();
        SimulationCanvas canvas = new SimulationCanvas(trafficEngine);
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

        controlPanel.getBtnAddRoad().setOnAction(e -> {
            for (int r = 0; r < 3; r++) {
                trafficEngine.addCustomNode(new IntersectionNode(99, nextGridX, r));
            }
            nextGridX++;
            canvas.draw();
        });

        // ==============================================================
        // SỬA NÚT SPAWN: CHO XE XUẤT HIỆN Ở NGÃ TƯ RANDOM VÀ TỰ TÌM ĐƯỜNG
        // ==============================================================
        controlPanel.getBtnSpawn().setOnAction(e -> {
            int count = controlPanel.getSpinnerSpawnCount().getValue();
            String type = controlPanel.getComboVehicleType().getValue();
            String[] rawTypes = {"CAR", "AMBULANCE", "MOTORBIKE", "FIRE_TRUCK", "BUS"};
            java.util.Random rand = new java.util.Random();

            java.util.List<IntersectionNode> nodes = trafficEngine.getIntersectionNodes();

            if (nodes.isEmpty()) {
                System.out.println("⚠️ Lỗi: Bản đồ chưa có ngã tư nào, hãy bấm Add Road trước!");
                return;
            }

            if (nodes.size() >= 2) {
                for (int i = 0; i < count; i++) {
                    String finalType = type.equalsIgnoreCase("All") ? rawTypes[rand.nextInt(rawTypes.length)] : type.toUpperCase();

                    // 1. CHỌN NGÃ TƯ XUẤT PHÁT NGẪU NHIÊN
                    IntersectionNode start = nodes.get(rand.nextInt(nodes.size()));

                    // 2. LỌC RA CÁC NGÃ TƯ HÀNG XÓM (Sát vách) ĐỂ ÉP XE CHẠY DỌC ĐƯỜNG NHỰA
                    java.util.List<IntersectionNode> neighbors = new java.util.ArrayList<>();
                    for (IntersectionNode n : nodes) {
                        int dx = Math.abs(n.getGridX() - start.getGridX());
                        int dy = Math.abs(n.getGridY() - start.getGridY());
                        // Chỉ lấy những node cách đúng 1 ô (theo phương ngang hoặc dọc)
                        if ((dx == 1 && dy == 0) || (dx == 0 && dy == 1)) {
                            neighbors.add(n);
                        }
                    }

                    // 3. CHỐT MỤC TIÊU LÀ MỘT TRONG SỐ CÁC HÀNG XÓM
                    IntersectionNode target;
                    if (!neighbors.isEmpty()) {
                        target = neighbors.get(rand.nextInt(neighbors.size()));
                    } else {
                        // Nếu lỡ đẻ ra ở đường cụt thì ép nó target lại chính nó tạm thời
                        target = start;
                    }

                    String id = finalType + "_" + System.currentTimeMillis() + "_" + i;

                    switch(finalType) {
                        case "CAR": vehicleManager.addVehicle(new Car(id, start, target, nodes)); break;
                        case "BUS": vehicleManager.addVehicle(new Bus(id, start, target, nodes)); break;
                        case "AMBULANCE": vehicleManager.addVehicle(new Ambulance(id, start, target, nodes)); break;
                        case "FIRE_TRUCK": vehicleManager.addVehicle(new FireTruck(id, start, target, nodes)); break;
                        case "MOTORBIKE": vehicleManager.addVehicle(new Motorbike(id, start, target, nodes)); break;
                    }
                }
                canvas.draw();
            }
        });
        // (Giữ nguyên các nút chuyển Rectangle/Image của ông)
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
        // GAME LOOP ĐÃ ĐƯỢC TẨY NÃO: TRAO QUYỀN CHO BỘ TƯ LỆNH AI
        // ==============================================================
        new Thread(() -> {
            while (true) {
                if (!isPaused[0]) {
                    // Lấy màu đèn hiện tại (giả định engine của ông đang dùng "GREEN", "RED"...)
                    String currentLight = trafficEngine.getCurrentMockColor();
                    if(currentLight == null) currentLight = "GREEN"; // Đề phòng lỗi null

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