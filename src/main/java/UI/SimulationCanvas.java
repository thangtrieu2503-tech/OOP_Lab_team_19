package UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

import java.util.List;

import MapSystem.map.Intersection;
import MapSystem.map.Road;
import MapSystem.map.RoadGraph;
import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.vehicle.VehicleManager;
import MapSystem.light.TrafficLight;

import VehicleSystem.vehicle.type.Ambulance;
import VehicleSystem.vehicle.type.Bus;
import VehicleSystem.vehicle.type.FireTruck;
import VehicleSystem.vehicle.type.Motorbike;

public class SimulationCanvas extends Canvas {

    private RoadGraph map;
    private VehicleManager vehicleManager;

    private double scale = 1.0;
    private double panOffsetX = 0.0;
    private double panOffsetY = 0.0;
    private double dragStartX, dragStartY;

    private boolean isRectangleMode = true;

    private javafx.scene.image.Image imgCar;
    private javafx.scene.image.Image imgMotorbike;
    private javafx.scene.image.Image imgAmbulance;
    private javafx.scene.image.Image imgFireTruck;
    private javafx.scene.image.Image imgBus;

    public SimulationCanvas(double width, double height, RoadGraph map, VehicleManager vehicleManager) {
        super(width, height);
        this.map = map;
        this.vehicleManager = vehicleManager;

        this.setOnMousePressed(e -> {
            dragStartX = e.getX() - panOffsetX;
            dragStartY = e.getY() - panOffsetY;
        });

        this.setOnMouseDragged(e -> {
            panOffsetX = e.getX() - dragStartX;
            panOffsetY = e.getY() - dragStartY;
            render();
        });

        try {
            imgCar = new javafx.scene.image.Image(new java.io.File("src/main/resources/images/car.png").toURI().toString());
            imgMotorbike = new javafx.scene.image.Image(new java.io.File("src/main/resources/images/motorbike.png").toURI().toString());
            imgAmbulance = new javafx.scene.image.Image(new java.io.File("src/main/resources/images/ambulance.png").toURI().toString());
            imgFireTruck = new javafx.scene.image.Image(new java.io.File("src/main/resources/images/firetruck.png").toURI().toString());
            imgBus = new javafx.scene.image.Image(new java.io.File("src/main/resources/images/bus.png").toURI().toString());

            System.out.println("✅ Đã nạp thành công toàn bộ ảnh xe!");
        } catch (Exception e) {
            System.out.println("[⚠️ Đồ họa] Không nạp được ảnh xe: " + e.getMessage());
        }
    }

    public void zoomIn() {
        if (this.scale < 3.5) { this.scale *= 1.1; render(); }
    }

    public void zoomOut() {
        if (this.scale > 0.5) { this.scale /= 1.1; render(); }
    }

    public void setRectangleMode(boolean mode) {
        this.isRectangleMode = mode;
        render();
    }

    private void drawSingleLight(GraphicsContext gc, double x, double y, double d, String state, boolean isHorizontal, String displayTimer) {
        double gap = Math.max(1, d / 4.0);
        Color off = Color.web("#3C3C3C");

        if (isHorizontal) {
            gc.setFill(Color.web("#141414"));
            gc.fillRoundRect(x - gap, y - gap, (d + gap) * 4.0 + gap, d + (gap * 2.0), gap, gap);

            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : off); gc.fillOval(x, y, d, d);
            gc.setFill(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); gc.fillOval(x + d + gap, y, d, d);
            gc.setFill(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); gc.fillOval(x + (d + gap) * 2.0, y, d, d);

            double boxX = x + (d + gap) * 3.0;
            gc.setFill(Color.BLACK); gc.fillRect(boxX, y, d, d);
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : (state.equalsIgnoreCase("GREEN") ? Color.GREEN : Color.YELLOW));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, Math.max(d - 1, 9)));
            gc.fillText(displayTimer, boxX + (1 * scale), y + d - (1 * scale));
        } else {
            gc.setFill(Color.web("#141414"));
            gc.fillRoundRect(x - gap, y - gap, d + (gap * 2.0), (d + gap) * 4.0 + gap, gap, gap);

            gc.setFill(Color.BLACK); gc.fillRect(x, y, d, d);
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : (state.equalsIgnoreCase("GREEN") ? Color.GREEN : Color.YELLOW));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, Math.max(d - 1, 9)));
            gc.fillText(displayTimer, x + (1 * scale), y + d - (1 * scale));

            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : off); gc.fillOval(x, y + d + gap, d, d);
            gc.setFill(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); gc.fillOval(x, y + (d + gap) * 2.0, d, d);
            gc.setFill(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); gc.fillOval(x, y + (d + gap) * 3.0, d, d);
        }
    }

    private void drawAllTrafficLights(GraphicsContext gc, List<Intersection> currentNodes) {
        double dynamicD = 11.0 * scale; if (dynamicD < 6) dynamicD = 6;
        double gap = Math.max(1, dynamicD / 4.0);
        double lightBoxLength = (dynamicD + gap) * 4.0 + gap;

        double roadHalfWidth = (160.0 * scale) / 2.0;
        double laneCenter = roadHalfWidth / 2.0;
        double stopLine = roadHalfWidth + (2.0 * scale);

        for (Intersection n1 : currentNodes) {
            double cx = n1.getPosition().getX() * scale + panOffsetX;
            double cy = n1.getPosition().getY() * scale + panOffsetY;

            boolean hasRight = false; boolean hasLeft = false; boolean hasBottom = false; boolean hasTop = false;
            for (Intersection n2 : currentNodes) {
                if (n2.getPosition().getX() > n1.getPosition().getX() + 5 && Math.abs(n2.getPosition().getY() - n1.getPosition().getY()) < 5) hasRight = true;
                if (n2.getPosition().getX() < n1.getPosition().getX() - 5 && Math.abs(n2.getPosition().getY() - n1.getPosition().getY()) < 5) hasLeft = true;
                if (n2.getPosition().getY() > n1.getPosition().getY() + 5 && Math.abs(n2.getPosition().getX() - n1.getPosition().getX()) < 5) hasBottom = true;
                if (n2.getPosition().getY() < n1.getPosition().getY() - 5 && Math.abs(n2.getPosition().getX() - n1.getPosition().getX()) < 5) hasTop = true;
            }

            var controller = n1.getTrafficController();
            if (controller == null || controller.getLights().isEmpty()) continue;

            List<TrafficLight> realLights = controller.getLights();
            TrafficLight lightEW = realLights.get(0);
            TrafficLight lightNS = realLights.size() > 1 ? realLights.get(1) : lightEW;

            String stateEW = lightEW.getCurrentState().name();
            String timerEW = lightEW.getDisplayTimer();
            String stateNS = lightNS.getCurrentState().name();
            String timerNS = lightNS.getDisplayTimer();

            if (hasTop) drawSingleLight(gc, cx - laneCenter - (lightBoxLength / 2.0), cy - stopLine - dynamicD, dynamicD, stateNS, true, timerNS);
            if (hasBottom) drawSingleLight(gc, cx + laneCenter - (lightBoxLength / 2.0), cy + stopLine, dynamicD, stateNS, true, timerNS);
            if (hasLeft) drawSingleLight(gc, cx - stopLine - dynamicD, cy + laneCenter - (lightBoxLength / 2.0), dynamicD, stateEW, false, timerEW);
            if (hasRight) drawSingleLight(gc, cx + stopLine, cy - laneCenter - (lightBoxLength / 2.0), dynamicD, stateEW, false, timerEW);
        }
    }

    public void render() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();
        List<Intersection> currentNodes = map.getIntersections();

        // ===============================================================
        // 🟩 VẼ ĐƯỜNG BẰNG CODE (LUÔN LUÔN VẼ)
        // ===============================================================
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, panelWidth, panelHeight);

        double circleDiameter = 160.0 * scale;
        double roadWidth = 160.0 * scale;
        Color roadColor = Color.web("#555555");

        for (Road road : map.getRoads()) {
            double sx = road.getStartNode().getPosition().getX() * scale + panOffsetX;
            double sy = road.getStartNode().getPosition().getY() * scale + panOffsetY;
            double ex = road.getEndNode().getPosition().getX() * scale + panOffsetX;
            double ey = road.getEndNode().getPosition().getY() * scale + panOffsetY;

            gc.setStroke(roadColor);
            gc.setLineWidth(roadWidth);
            gc.setLineCap(StrokeLineCap.BUTT);
            gc.strokeLine(sx, sy, ex, ey);
        }

        for (Intersection node : currentNodes) {
            double cx = node.getPosition().getX() * scale + panOffsetX;
            double cy = node.getPosition().getY() * scale + panOffsetY;
            gc.setFill(roadColor);
            gc.fillOval(cx - circleDiameter / 2.0, cy - circleDiameter / 2.0, circleDiameter, circleDiameter);
        }

        gc.setFill(Color.WHITE);
        Intersection n00 = null, n01 = null, n02 = null, n10 = null, n11 = null, n12 = null, n20 = null, n21 = null, n22 = null;
        for (Intersection n : currentNodes) {
            if (n.getId().equals("Node_0_0")) n00 = n; if (n.getId().equals("Node_0_1")) n01 = n; if (n.getId().equals("Node_0_2")) n02 = n;
            if (n.getId().equals("Node_1_0")) n10 = n; if (n.getId().equals("Node_1_1")) n11 = n; if (n.getId().equals("Node_1_2")) n12 = n;
            if (n.getId().equals("Node_2_0")) n20 = n; if (n.getId().equals("Node_2_1")) n21 = n; if (n.getId().equals("Node_2_2")) n22 = n;
        }

        double rOffset = roadWidth / 2.0;
        double blockCorner = 35.0 * scale;

        if (n00 != null && n11 != null && n22 != null) {
            double x0 = n00.getPosition().getX() * scale + panOffsetX; double x1 = n01.getPosition().getX() * scale + panOffsetX; double x2 = n02.getPosition().getX() * scale + panOffsetX;
            double y0 = n00.getPosition().getY() * scale + panOffsetY; double y1 = n10.getPosition().getY() * scale + panOffsetY; double y2 = n20.getPosition().getY() * scale + panOffsetY;

            double xA = x0 + rOffset; double yA = y0 + rOffset; double wA = (x1 - rOffset) - xA; double hA = (y1 - rOffset) - yA;
            if (wA > 0 && hA > 0) gc.fillRoundRect(xA, yA, wA, hA, blockCorner, blockCorner);

            double xB = x1 + rOffset; double yB = y0 + rOffset; double wB = (x2 - rOffset) - xB; double hB = (y1 - rOffset) - yB;
            if (wB > 0 && hB > 0) gc.fillRoundRect(xB, yB, wB, hB, blockCorner, blockCorner);

            double xC = x0 + rOffset; double yC = y1 + rOffset; double wC = (x1 - rOffset) - xC; double hC = (y2 - rOffset) - yC;
            if (wC > 0 && hC > 0) gc.fillRoundRect(xC, yC, wC, hC, blockCorner, blockCorner);

            double xD = x1 + rOffset; double yD = y1 + rOffset; double wD = (x2 - rOffset) - xD; double hD = (y2 - rOffset) - yD;
            if (wD > 0 && hD > 0) gc.fillRoundRect(xD, yD, wD, hD, blockCorner, blockCorner);
        }

        for (Road road : map.getRoads()) {
            double sx = road.getStartNode().getPosition().getX() * scale + panOffsetX;
            double sy = road.getStartNode().getPosition().getY() * scale + panOffsetY;
            double ex = road.getEndNode().getPosition().getX() * scale + panOffsetX;
            double ey = road.getEndNode().getPosition().getY() * scale + panOffsetY;

            double dx = ex - sx; double dy = ey - sy; double len = Math.sqrt(dx * dx + dy * dy);
            if (len == 0) continue;

            double ux = dx / len; double uy = dy / len;
            double rStop = circleDiameter / 2.0;
            double cutSx = sx + ux * rStop; double cutSy = sy + uy * rStop;
            double cutEx = ex - ux * rStop; double cutEy = ey - uy * rStop;

            gc.setStroke(Color.web("#FFC107"));
            gc.setLineWidth(2.5 * scale);
            gc.strokeLine(cutSx, cutSy, cutEx, cutEy);

            gc.setStroke(Color.WHITE); gc.setLineWidth(1.2 * scale); gc.setLineDashes(10 * scale, 10 * scale);
            double nx = -uy; double ny = ux;
            double singleLaneW = (80.0 * scale) / 3.0; double lane1 = singleLaneW; double lane2 = singleLaneW * 2.0;

            gc.strokeLine(cutSx + nx * lane1, cutSy + ny * lane1, cutEx + nx * lane1, cutEy + ny * lane1);
            gc.strokeLine(cutSx + nx * lane2, cutSy + ny * lane2, cutEx + nx * lane2, cutEy + ny * lane2);
            gc.strokeLine(cutSx - nx * lane1, cutSy - ny * lane1, cutEx - nx * lane1, cutEy - ny * lane1);
            gc.strokeLine(cutSx - nx * lane2, cutSy - ny * lane2, cutEx - nx * lane2, cutEy - ny * lane2);
            gc.setLineDashes((double[]) null);
        }

        for (Intersection node : currentNodes) {
            double cx = node.getPosition().getX() * scale + panOffsetX;
            double cy = node.getPosition().getY() * scale + panOffsetY;
            gc.setFill(Color.web("#28A745"));
            double centerIslandD = 45.0 * scale;
            gc.fillOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);
            gc.setStroke(Color.WHITE); gc.setLineWidth(2.0 * scale);
            gc.strokeOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);
        }

        // ===============================================================
        // 🚥 VẼ ĐÈN GIAO THÔNG CÓ COUNTDOWN
        // ===============================================================
        drawAllTrafficLights(gc, currentNodes);

        // ===============================================================
        // 🚗 VẼ XE CỘ (LÚC NÀY NÚT BẤM CHỈ CÓ TÁC DỤNG VỚI XE)
        // ===============================================================
        for (Vehicle v : vehicleManager.getVehicles()) {
            double vx = v.getPosition().getX() * scale + panOffsetX;
            double vy = v.getPosition().getY() * scale + panOffsetY;

            double w = v.getWidth() * scale;
            double h = v.getLength() * scale;

            Color rectColor = Color.DODGERBLUE;
            javafx.scene.image.Image activeSprite = null;

            if (v instanceof Ambulance) { rectColor = Color.WHITE; activeSprite = imgAmbulance; }
            else if (v instanceof FireTruck) { rectColor = Color.CRIMSON; activeSprite = imgFireTruck; }
            else if (v instanceof Bus) { rectColor = Color.DARKBLUE; activeSprite = imgBus; }
            else if (v instanceof Motorbike) { rectColor = Color.ORANGE; activeSprite = imgMotorbike; }
            else { rectColor = Color.DARKSLATEGRAY; activeSprite = imgCar; }

            double angle = v.getAngle();

            gc.save();
            gc.translate(vx, vy);
            gc.rotate(angle);

            // TÁCH CHẾ ĐỘ: Nếu đang chọn RectangleMode HOẶC xe chưa load được ảnh -> Vẽ hình hộp phẳng
            if (isRectangleMode || activeSprite == null) {
                gc.setFill(rectColor);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.0 * scale);
                gc.fillRoundRect(-h / 2.0, -w / 2.0, h, w, 4 * scale, 4 * scale);
                gc.strokeRoundRect(-h / 2.0, -w / 2.0, h, w, 4 * scale, 4 * scale);

                gc.setFill(Color.web("#1A1C20"));
                gc.fillRoundRect(h / 2.0 - (8 * scale), -w / 2.0 + (2 * scale), 6 * scale, w - (4 * scale), 2 * scale, 2 * scale);
            } else {
                // CHẾ ĐỘ IMAGE: In ảnh xe thật ra đường
                gc.drawImage(activeSprite, -h / 2.0, -w / 2.0, h, w);
            }

            // Đèn LED xanh đỏ chớp nháy của xe ưu tiên (Luôn hiển thị kể cả trên ảnh xe)
            if (v instanceof Ambulance || v instanceof FireTruck) {
                boolean toggle = (System.currentTimeMillis() / 150) % 2 == 0;
                double ledSize = 4.0 * scale;

                gc.setFill(toggle ? Color.RED : Color.BLUE);
                gc.fillOval(-h / 4.0, -w / 2.0 + (1 * scale), ledSize, ledSize);

                gc.setFill(toggle ? Color.BLUE : Color.RED);
                gc.fillOval(-h / 4.0, w / 2.0 - ledSize - (1 * scale), ledSize, ledSize);
            }

            gc.restore();
        }
    }
}