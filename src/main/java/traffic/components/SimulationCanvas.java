package traffic.components;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import traffic.map.IntersectionNode;
import java.util.List;

public class SimulationCanvas extends Canvas {

    private TrafficEngine engine;
    private double zoomScale = 1.0;
    private double mapDrawX = 0, mapDrawY = 0, mapDrawW = 800, mapDrawH = 550;
    private boolean isRectangleMode = true;

    private double panOffsetX = 0, panOffsetY = 0;
    private double dragStartX, dragStartY;

    public SimulationCanvas(TrafficEngine engine) {
        this.engine = engine;
        this.setWidth(800);
        this.setHeight(550);

        this.setOnMousePressed(e -> {
            dragStartX = e.getX() - panOffsetX;
            dragStartY = e.getY() - panOffsetY;
        });

        this.setOnMouseDragged(e -> {
            panOffsetX = e.getX() - dragStartX;
            panOffsetY = e.getY() - dragStartY;
            draw(); // Ép vẽ lại liên tục khi kéo chuột di chuyển Map (Pan)
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

        double baseMapWidth = 1000.0;
        double baseMapHeight = 1000.0;
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
        
        // Vẽ màu cỏ nền sa hình xanh mướt
        gc.setFill(Color.web("#D4EDDA"));
        gc.fillRect(0, 0, panelWidth, panelHeight);

        double step = 320;          
        double startOffset = 160;   
        double circleDiameter = 200; 
        double roadWidth = 140;      
        Color roadColor = Color.web("#555555"); 

        List<IntersectionNode> currentNodes = engine.getIntersectionNodes();

        // ================= BƯỚC 1: VẼ CÁC TRỤC ĐƯỜNG 3 LÀN MỖI CHIỀU =================
        for (IntersectionNode n1 : currentNodes) {
            double cx1 = mapDrawX + (startOffset + n1.getGridX() * step) * scaleX;
            double cy1 = mapDrawY + (startOffset + n1.getGridY() * step) * scaleY;

            for (IntersectionNode n2 : currentNodes) {
                double cx2 = mapDrawX + (startOffset + n2.getGridX() * step) * scaleX;
                double cy2 = mapDrawY + (startOffset + n2.getGridY() * step) * scaleY;
                double rActual = (circleDiameter / 2.0) * scaleX;

                // ---- TRỤC ĐƯỜNG NGANG ----
                if (n2.getGridX() == n1.getGridX() + 1 && n2.getGridY() == n1.getGridY()) {
                    double rw = cx2 - cx1;
                    double rh = roadWidth * scaleY;
                    
                    gc.setFill(roadColor); 
                    gc.fillRect(cx1, cy1 - rh / 2.0, rw, rh);

                    gc.setFill(Color.web("#FFC107"));
                    gc.fillRect(cx1 + rActual, cy1 - (2 * scaleY), rw - (rActual * 2), 4 * scaleY);

                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(Math.max(0.5, 1 * scaleX));
                    gc.setLineDashes(new double[]{10 * scaleX, 10 * scaleX});

                    double laneDisplacement = (roadWidth / 6.0) * scaleY; 
                    gc.strokeLine(cx1 + rActual, cy1 - laneDisplacement, cx2 - rActual, cy1 - laneDisplacement);
                    gc.strokeLine(cx1 + rActual, cy1 - laneDisplacement * 2, cx2 - rActual, cy1 - laneDisplacement * 2);
                    gc.strokeLine(cx1 + rActual, cy1 + laneDisplacement, cx2 - rActual, cy1 + laneDisplacement);
                    gc.strokeLine(cx1 + rActual, cy1 + laneDisplacement * 2, cx2 - rActual, cy1 + laneDisplacement * 2);

                    gc.setLineDashes(null); 
                }

                // ---- TRỤC ĐƯỜNG ĐỨNG ----
                if (n2.getGridY() == n1.getGridY() + 1 && n2.getGridX() == n1.getGridX()) {
                    double rw = roadWidth * scaleX; 
                    double rh = cy2 - cy1;
                    
                    gc.setFill(roadColor); 
                    gc.fillRect(cx1 - rw / 2.0, cy1, rw, rh);

                    gc.setFill(Color.web("#FFC107"));
                    gc.fillRect(cx1 - (2 * scaleX), cy1 + rActual, 4 * scaleX, rh - (rActual * 2));

                    gc.setStroke(Color.WHITE);
                    gc.setLineWidth(Math.max(0.5, 1 * scaleX));
                    gc.setLineDashes(new double[]{10 * scaleY, 10 * scaleY});

                    double laneDisplacement = (roadWidth / 6.0) * scaleX;
                    gc.strokeLine(cx1 - laneDisplacement, cy1 + rActual, cx1 - laneDisplacement, cy2 - rActual);
                    gc.strokeLine(cx1 - laneDisplacement * 2, cy1 + rActual, cx1 - laneDisplacement * 2, cy2 - rActual);
                    gc.strokeLine(cx1 + laneDisplacement, cy1 + rActual, cx1 + laneDisplacement, cy2 - rActual);
                    gc.strokeLine(cx1 + laneDisplacement * 2, cy1 + rActual, cx1 + laneDisplacement * 2, cy2 - rActual);

                    gc.setLineDashes(null);
                }
            }
        }

        // ================= BƯỚC 2: VẼ BÙNG BINH + ĐẢO CỎ TÂM XỊN MỊN =================
        double rComm = circleDiameter * scaleX; 
        for (IntersectionNode node : currentNodes) {
            double cx = mapDrawX + (startOffset + node.getGridX() * step) * scaleX;
            double cy = mapDrawY + (startOffset + node.getGridY() * step) * scaleY;
            
            gc.setFill(roadColor);
            gc.fillOval(cx - rComm / 2.0, cy - rComm / 2.0, rComm, rComm);

            // BẬT LẠI ĐẢO CỎ CHO ĐÚNG SA HÌNH MẪU CỦA ÔNG
            gc.setFill(Color.web("#28A745"));
            double centerIslandD = 75 * scaleX; 
            gc.fillOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);
            
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.5 * scaleX);
            gc.strokeOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);
        }

        drawAllTrafficLights(gc, scaleX, scaleY);
        drawVehicles(gc, scaleX, scaleY);
    }

    private void drawSingleLight(GraphicsContext gc, double x, double y, double d, String state, boolean isHorizontal) {
        double gap = Math.max(1, d / 4.0);
        Color off = Color.web("#3C3C3C");
        int countdown = engine.getLightCountdown();

        if (isHorizontal) {
            gc.setFill(Color.web("#141414"));
            gc.fillRoundRect(x - gap, y - gap, (d + gap) * 4.0 + gap, d + (gap * 2.0), gap, gap);
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : off); gc.fillOval(x, y, d, d);
            gc.setFill(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); gc.fillOval(x + d + gap, y, d, d);
            gc.setFill(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); gc.fillOval(x + (d + gap) * 2.0, y, d, d);

            double boxX = x + (d + gap) * 3.0;
            gc.setFill(Color.BLACK); gc.fillRect(boxX, y, d, d);
            gc.setFill(Color.WHITE); gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, Math.max(d - 2, 10)));
            gc.fillText(String.valueOf(countdown), boxX + 2, y + d - 2);
        } else {
            gc.setFill(Color.web("#141414"));
            gc.fillRoundRect(x - gap, y - gap, d + (gap * 2.0), (d + gap) * 4.0 + gap, gap, gap);

            gc.setFill(Color.BLACK); gc.fillRect(x, y, d, d);
            gc.setFill(Color.WHITE); gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, Math.max(d - 2, 10)));
            gc.fillText(String.valueOf(countdown), x + 2, y + d - 2);

            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : off); gc.fillOval(x, y + d + gap, d, d);
            gc.setFill(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); gc.fillOval(x, y + (d + gap) * 2.0, d, d);
            gc.setFill(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); gc.fillOval(x, y + (d + gap) * 3.0, d, d);
        }
    }

    private void drawAllTrafficLights(GraphicsContext gc, double scaleX, double scaleY) {
        String stateEW = engine.getCurrentMockColor();
        String stateNS = stateEW.equals("RED") ? "GREEN" : (stateEW.equals("GREEN") ? "YELLOW" : "RED");

        double dynamicD = 12.0 * scaleX; if (dynamicD < 6) dynamicD = 6;
        double gap = Math.max(1, dynamicD / 4.0);
        double lightBoxLength = (dynamicD + gap) * 4.0 + gap;

        double step = 320; double startOffset = 160;
        double rActual = (200.0 / 2.0) * scaleX; // Đồng bộ theo bùng binh bự 200px
        double currentRoadW = 140.0 * scaleY; double laneCenter = currentRoadW / 4.0;
        double paddingStop = rActual + (6.0 * scaleY);

        for (IntersectionNode n1 : engine.getIntersectionNodes()) {
            double cx = mapDrawX + (startOffset + n1.getGridX() * step) * scaleX;
            double cy = mapDrawY + (startOffset + n1.getGridY() * step) * scaleY;

            boolean hasRight = false; boolean hasLeft = false; boolean hasBottom = false; boolean hasTop = false;
            for (IntersectionNode n2 : engine.getIntersectionNodes()) {
                if (n2.getGridX() == n1.getGridX() + 1 && n2.getGridY() == n1.getGridY()) hasRight = true;
                if (n2.getGridX() == n1.getGridX() - 1 && n2.getGridY() == n1.getGridY()) hasLeft = true;
                if (n2.getGridY() == n1.getGridY() + 1 && n2.getGridX() == n1.getGridX()) hasBottom = true;
                if (n2.getGridY() == n1.getGridY() - 1 && n2.getGridX() == n1.getGridX()) hasTop = true;
            }

            if (hasTop) drawSingleLight(gc, cx - laneCenter - (lightBoxLength / 2.0), cy - paddingStop - dynamicD, dynamicD, stateNS, true); 
            if (hasBottom) drawSingleLight(gc, cx + laneCenter - (lightBoxLength / 2.0), cy + paddingStop, dynamicD, stateNS, true); 
            if (hasLeft) drawSingleLight(gc, cx - paddingStop - dynamicD, cy - laneCenter - (lightBoxLength / 2.0), dynamicD, stateEW, false); 
            if (hasRight) drawSingleLight(gc, cx + rActual + (10.0 * scaleY), cy + laneCenter - (lightBoxLength / 2.0), dynamicD, stateEW, false); 
        }
    }

    private void drawVehicles(GraphicsContext gc, double scaleX, double scaleY) {
        for (MockVehicle vehicle : engine.getVehicleList()) {
            
            double virtualW = 42;   
            double virtualH = 20;  
            Color rectColor = Color.DODGERBLUE;
            double spriteSizeFactor = 0.09; 

            switch (vehicle.getType()) {
                case "BUS":
                    virtualW = 75; virtualH = 25; rectColor = Color.DARKBLUE; spriteSizeFactor = 0.15;
                    break;
                case "TRUCK":
                    virtualW = 68; virtualH = 24; rectColor = Color.DARKSLATEGRAY; spriteSizeFactor = 0.14;
                    break;
                case "FIRE_TRUCK":
                    virtualW = 65; virtualH = 25; rectColor = Color.CRIMSON; spriteSizeFactor = 0.13;
                    break;
                case "AMBULANCE":
                    virtualW = 54; virtualH = 22; rectColor = Color.WHITE; spriteSizeFactor = 0.11;
                    break;
                case "MOTORBIKE":
                case "BIKE":
                    virtualW = 24; virtualH = 11; rectColor = Color.ORANGE; spriteSizeFactor = 0.05;
                    break;
            }

            // Ép kích thước xe co dãn tuyến tính tuyệt đối theo tỉ lệ thu phóng bản đồ
            double targetW = virtualW * scaleX;
            double targetH = virtualH * scaleY;

            // Tính toán vị trí vẽ theo hệ trục ảo World coordinates nhân hệ số tỉ lệ
            double vx = mapDrawX + (vehicle.getX() * scaleX);
            double vy = mapDrawY + (vehicle.getY() * scaleY);
            
            double centerX = vx + targetW / 2.0;
            double centerY = vy + targetH / 2.0;

            gc.save();
            Rotate r = new Rotate(vehicle.getAngle(), centerX, centerY);
            gc.setTransform(r.getMxx(), r.getMxy(), r.getMyx(), r.getMyy(), r.getTx(), r.getTy());

            if (isRectangleMode) {
                gc.setFill(rectColor);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.5);
                gc.fillRoundRect(vx, vy, targetW, targetH, 6 * scaleX, 6 * scaleY);
                gc.strokeRoundRect(vx, vy, targetW, targetH, 6 * scaleX, 6 * scaleY);
            } else {
                javafx.scene.image.Image sprite = traffic.render.Renderer.getSprite(vehicle.getType());
                if (sprite != null) {
                    // Dịch tâm để vẽ ảnh không bao giờ bị dạt lệch quỹ đạo
                    gc.drawImage(sprite, centerX - targetW / 2.0, centerY - targetH / 2.0, targetW, targetH);
                } else {
                    gc.setFill(rectColor);
                    gc.fillRect(vx, vy, targetW, targetH);
                }
            }

            // Đèn LED xe ưu tiên
            if (vehicle.getType().equals("AMBULANCE") || vehicle.getType().equals("FIRE_TRUCK")) {
                boolean toggle = (System.currentTimeMillis() / 150) % 2 == 0;
                double ledSize = 5.0 * scaleX;
                gc.setFill(toggle ? Color.RED : Color.BLUE);
                gc.fillOval(centerX - ledSize, centerY - ledSize / 2.0, ledSize, ledSize);
                gc.setFill(toggle ? Color.BLUE : Color.RED);
                gc.fillOval(centerX, centerY - ledSize / 2.0, ledSize, ledSize);
            }
            gc.restore();
        }
    }

    public void setRectangleMode(boolean isRectangleMode) {
        this.isRectangleMode = isRectangleMode;
        draw();
    }
}