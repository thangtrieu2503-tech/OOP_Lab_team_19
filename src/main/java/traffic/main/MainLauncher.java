package traffic.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import traffic.components.TrafficEngine;
import traffic.components.SimulationCanvas;
import traffic.components.ControlPanel;
import traffic.components.MockVehicle;
import traffic.map.IntersectionNode;

import java.util.List;

public class MainLauncher extends Application {

    private static int nextGridX = 3;
    private final boolean[] isPaused = {false};

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

        controlPanel.getBtnRemoveNode().setOnAction(e -> {
            List<IntersectionNode> nodes = trafficEngine.getIntersectionNodes();
            if (!nodes.isEmpty()) {
                IntersectionNode removed = nodes.remove(nodes.size() - 1);
                if (removed.getGridX() >= 3 && (nodes.isEmpty() || nodes.get(nodes.size() - 1).getGridX() < removed.getGridX())) {
                    nextGridX = removed.getGridX();
                }
                canvas.draw();
            }
        });

        // FIX SỰ KIỆN NÚT SPAWN XE CHUẨN TỌA ĐỘ LÀN MỚI
        controlPanel.getBtnSpawn().setOnAction(e -> {
            int count = controlPanel.getSpinnerSpawnCount().getValue();
            String type = controlPanel.getComboVehicleType().getValue();
            String[] rawTypes = {"CAR", "AMBULANCE", "MOTORBIKE", "FIRE_TRUCK", "BUS"};
            java.util.Random rand = new java.util.Random();

            for (int i = 0; i < count; i++) {
                String finalType = type.equalsIgnoreCase("All") ? rawTypes[rand.nextInt(rawTypes.length)] : type.toUpperCase();
                int tailOffset = i * 60; // Khoảng cách giãn đuôi xe xếp hàng
                
                // Đẻ xe ở vách lề trái, chạy làn giữa hướng sang phải (Y = 535)
                trafficEngine.getVehicleList().add(new MockVehicle(finalType, -50 - tailOffset, 535, 3, 0));
            }
            canvas.draw();
        }); 

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

        // NẠP SẴN CÁC XE BAN ĐẦU CHẠY THỬ NGHIỆM ĐÚNG LÀN GIỮA ĐƯỜNG 140PX
        trafficEngine.getVehicleList().add(new MockVehicle("CAR", 0, 535, 3, 0));          // Trái -> Phải
        trafficEngine.getVehicleList().add(new MockVehicle("AMBULANCE", 1000, 465, 3, 180)); // Phải -> Trái
        trafficEngine.getVehicleList().add(new MockVehicle("MOTORBIKE", 535, 0, 3, 90));     // Trên -> Dưới
        trafficEngine.getVehicleList().add(new MockVehicle("FIRE_TRUCK", 465, 1000, 3, 270)); // Dưới -> Trên

        BorderPane root = new BorderPane();
        root.setCenter(canvas);
        root.setLeft(controlPanel);

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setTitle("Traffic Simulation - Maven JavaFX Core");
        primaryStage.setScene(scene);
        primaryStage.show();

        canvas.draw();

        // ⚙️ GAME LOOP LIÊN TỤC 60 FPS ĐỘC LẬP - BỔ SUNG ĐỦ LUỒNG DI CHUYỂN 4 HƯỚNG
        new Thread(() -> {
            while (true) {
                if (!isPaused[0]) {
                    List<MockVehicle> list = trafficEngine.getVehicleList();
                    
                    for (MockVehicle vehicle : list) {
                        double angle = vehicle.getAngle();
                        double speed = vehicle.getSpeed();
                        
                        if (angle == 0) {
                            vehicle.setX(vehicle.getX() + speed);
                        } else if (angle == 180) {
                            vehicle.setX(vehicle.getX() - speed);
                        } else if (angle == 90) {
                            vehicle.setY(vehicle.getY() + speed);
                        } else if (angle == 270) {
                            vehicle.setY(vehicle.getY() - speed);
                        }
                    }

                    // Xóa xe khi chạy vượt quá biên giới hạn an toàn của bản đồ mở rộng động
                    trafficEngine.getVehicleList().removeIf(v ->
                        v.getX() > trafficEngine.getMapMaxX() || v.getX() < trafficEngine.getMapMinX() ||
                        v.getY() > trafficEngine.getMapMaxY() || v.getY() < trafficEngine.getMapMinY()
                    );
                    
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