package traffic.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.util.List;

// BÊ HỆ THỐNG MAP XỊN VÀO ĐÂY
import Map.map.Intersection;
import Map.map.Road;
import Map.map.RoadGraph;

public class SimulationCanvas extends Canvas {

    private double zoomScale = 1.0;
    private double mapDrawX = 0, mapDrawY = 0, mapDrawW = 800, mapDrawH = 550;
    private boolean isRectangleMode = true;

    private double panOffsetX = 0, panOffsetY = 0;
    private double dragStartX, dragStartY;

    // KHÔNG XÀI TRAFFIC ENGINE NỮA NHÉ
    public SimulationCanvas() {
        this.setWidth(800);
        this.setHeight(550);

        this.setOnMousePressed(e -> {
            dragStartX = e.getX() - panOffsetX;
            dragStartY = e.getY() - panOffsetY;
        });

        this.setOnMouseDragged(e -> {
            panOffsetX = e.getX() - dragStartX;
            panOffsetY = e.getY() - dragStartY;
            draw();
        });
    }

    public void zoomIn() {
        this.zoomScale += 0.1;
        if (this.zoomScale > 3.0) this.zoomScale = 3.0;
        draw();
    }

    public void zoomOut() {
        this.zoomScale -= 0.1;
        if (this.zoomScale < 0.5) this.zoomScale = 0.5;
        draw();
    }

    public void draw() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();
        double paddingLeft = 70;

        // Cho map base to ra để vẽ đường tự do
        double baseMapWidth = 2000.0;
        double baseMapHeight = 2000.0;
        double imgRatio = baseMapWidth / baseMapHeight;
        double panelRatio = (panelWidth - paddingLeft) / panelHeight;

        double baseW, baseH;
        if (imgRatio > panelRatio) {
            baseW = panelWidth - paddingLeft;
            baseH = (panelWidth - paddingLeft) / imgRatio;
        } else {
            baseH = panelHeight;
            baseW = panelHeight * imgRatio;
        }

        mapDrawW = baseW * zoomScale;
        mapDrawH = baseH * zoomScale;

        double centerX = paddingLeft + (panelWidth - paddingLeft) / 2.0;
        double centerY = panelHeight / 2.0;

        mapDrawX = centerX - (mapDrawW / 2.0) + panOffsetX;
        mapDrawY = centerY - (mapDrawH / 2.0) + panOffsetY;

        double scaleX = mapDrawW / baseMapWidth;
        double scaleY = mapDrawH / baseMapHeight;

        gc.setImageSmoothing(true);

        // Nền cỏ
        gc.setFill(Color.web("#D4EDDA"));
        gc.fillRect(0, 0, panelWidth, panelHeight);

        // Lấy bản đồ xịn từ MainLauncher
        RoadGraph roadGraph = traffic.main.MainLauncher.roadGraph;
        if (roadGraph == null) return; // Tránh lỗi chưa load map xong

        double circleDiameter = 200 * scaleX;
        double roadWidth = 140 * scaleX;
        Color roadColor = Color.web("#555555");

        // ================= BƯỚC 1: VẼ CÁC TRỤC ĐƯỜNG BẰNG VECTOR TỰ DO =================
        for (Road road : roadGraph.getRoads()) {
            double startX = mapDrawX + road.getStart().getX() * scaleX;
            double startY = mapDrawY + road.getStart().getY() * scaleY;
            double endX = mapDrawX + road.getEnd().getX() * scaleX;
            double endY = mapDrawY + road.getEnd().getY() * scaleY;

            // Dùng stroke nét to để vẽ đường nhựa (Bao luôn cả đường chéo)
            gc.setStroke(roadColor);
            gc.setLineWidth(roadWidth);
            gc.strokeLine(startX, startY, endX, endY);

            // Vẽ vạch kẻ đường màu vàng ở giữa
            gc.setStroke(Color.web("#FFC107"));
            gc.setLineWidth(4 * scaleX);
            gc.strokeLine(startX, startY, endX, endY);
        }

        // ================= BƯỚC 2: VẼ NGÃ TƯ + ĐẢO CỎ =================
        for (Intersection node : roadGraph.getIntersections()) {
            double cx = mapDrawX + node.getPosition().getX() * scaleX;
            double cy = mapDrawY + node.getPosition().getY() * scaleY;

            // Bùng binh nhựa
            gc.setFill(roadColor);
            gc.fillOval(cx - circleDiameter / 2.0, cy - circleDiameter / 2.0, circleDiameter, circleDiameter);

            // Đảo cỏ xanh giữa ngã tư
            gc.setFill(Color.web("#28A745"));
            double centerIslandD = 75 * scaleX;
            gc.fillOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);

            // Viền trắng cho đảo cỏ
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.5 * scaleX);
            gc.strokeOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);
        }

        // Tạm ẩn đèn giao thông đi, phần đèn mình sẽ tích hợp vào sau nếu rảnh
        // drawAllTrafficLights(gc, scaleX, scaleY);

        // ================= BƯỚC 3: VẼ XE AI =================
        drawVehicles(gc, scaleX, scaleY);
    }

    // ==============================================================
    // 🚀 HÀM VẼ XE LẤY TỪ VEHICLE MANAGER XỊN CỦA MAIN LAUNCHER
    // ==============================================================
    private void drawVehicles(GraphicsContext gc, double scaleX, double scaleY) {
        if (traffic.main.MainLauncher.vehicleManager == null) return;

        for (VehicleSystem.vehicle.Vehicle vehicle : traffic.main.MainLauncher.vehicleManager.getVehicles()) {

            double virtualW = 42;
            double virtualH = 20;
            Color rectColor = Color.DODGERBLUE;

            switch (vehicle.getType()) {
                case "BUS": virtualW = 75; virtualH = 25; rectColor = Color.DARKBLUE; break;
                case "TRUCK": virtualW = 68; virtualH = 24; rectColor = Color.DARKSLATEGRAY; break;
                case "FIRE_TRUCK": virtualW = 65; virtualH = 25; rectColor = Color.CRIMSON; break;
                case "AMBULANCE": virtualW = 54; virtualH = 22; rectColor = Color.WHITE; break;
                case "MOTORBIKE": virtualW = 24; virtualH = 11; rectColor = Color.ORANGE; break;
            }

            double targetW = virtualW * scaleX;
            double targetH = virtualH * scaleY;

            double screenCenterX = mapDrawX + (vehicle.getX() * scaleX);
            double screenCenterY = mapDrawY + (vehicle.getY() * scaleY);

            double drawX = screenCenterX - targetW / 2.0;
            double drawY = screenCenterY - targetH / 2.0;

            gc.save();
            Rotate r = new Rotate(vehicle.getAngle(), screenCenterX, screenCenterY);
            gc.setTransform(r.getMxx(), r.getMxy(), r.getMyx(), r.getMyy(), r.getTx(), r.getTy());

            if (isRectangleMode) {
                gc.setFill(rectColor);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.5);
                gc.fillRoundRect(drawX, drawY, targetW, targetH, 6 * scaleX, 6 * scaleY);
                gc.strokeRoundRect(drawX, drawY, targetW, targetH, 6 * scaleX, 6 * scaleY);

                if (vehicle.getType().equals("AMBULANCE") || vehicle.getType().equals("FIRE_TRUCK")) {
                    boolean toggle = (System.currentTimeMillis() / 150) % 2 == 0;
                    double ledSize = 5.0 * scaleX;
                    gc.setFill(toggle ? Color.RED : Color.BLUE);
                    gc.fillOval(screenCenterX - ledSize, screenCenterY - ledSize / 2.0, ledSize, ledSize);
                    gc.setFill(toggle ? Color.BLUE : Color.RED);
                    gc.fillOval(screenCenterX, screenCenterY - ledSize / 2.0, ledSize, ledSize);
                }
            } else {
                javafx.scene.image.Image sprite = traffic.render.Renderer.getSprite(vehicle.getType());
                if (sprite != null) {
                    gc.drawImage(sprite, drawX, drawY, targetW, targetH);
                } else {
                    gc.setFill(rectColor);
                    gc.fillRect(drawX, drawY, targetW, targetH);
                }
            }
            gc.restore();
        }
    }

    public void setRectangleMode(boolean isRectangleMode) {
        this.isRectangleMode = isRectangleMode;
        draw();
    }
}