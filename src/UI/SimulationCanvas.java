package UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Rotate;
import java.util.List;

import MapSystem.map.Intersection;
import MapSystem.map.Road;
import MapSystem.map.RoadGraph;
import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.vehicle.VehicleManager;
import VehicleSystem.vehicle.type.*;

public class SimulationCanvas extends Canvas {

    private RoadGraph map;
    private VehicleManager vehicleManager;

    // Các biến phục vụ Camera, Zoom & Kéo thả chuột di chuyển sa hình
    private double scale = 1.0;
    private double panOffsetX = 0.0;
    private double panOffsetY = 0.0;
    private double dragStartX, dragStartY;

    private boolean isRectangleMode = true;

    // Bộ nhớ đệm lưu ảnh xe cộ bốc trực tiếp từ tài nguyên hệ thống
    private javafx.scene.image.Image imgCar;
    private javafx.scene.image.Image imgMotorbike;
    private javafx.scene.image.Image imgAmbulance;
    private javafx.scene.image.Image imgFireTruck;
    private javafx.scene.image.Image imgBus;

    public SimulationCanvas(double width, double height, RoadGraph map, VehicleManager vehicleManager) {
        super(width, height);
        this.map = map;
        this.vehicleManager = vehicleManager;

        // Sự kiện click giữ chuột để dịch chuyển (Pan) bản đồ
        this.setOnMousePressed(e -> {
            dragStartX = e.getX() - panOffsetX;
            dragStartY = e.getY() - panOffsetY;
        });

        this.setOnMouseDragged(e -> {
            panOffsetX = e.getX() - dragStartX;
            panOffsetY = e.getY() - dragStartY;
            render();
        });

        // Tải ảnh từ thư mục tài nguyên tương thích với cấu hình pom chuẩn Maven
        try {
            imgCar = new javafx.scene.image.Image(getClass().getResourceAsStream("/image/car.png"));
            imgMotorbike = new javafx.scene.image.Image(getClass().getResourceAsStream("/image/motorbike.png"));
            imgAmbulance = new javafx.scene.image.Image(getClass().getResourceAsStream("/image/ambulance.png"));
            imgFireTruck = new javafx.scene.image.Image(getClass().getResourceAsStream("/image/firetruck.png"));
            imgBus = new javafx.scene.image.Image(getClass().getResourceAsStream("/image/bus.png"));
        } catch (Exception e) {
            System.out.println("[⚠️ Đồ họa] Không nạp được ảnh Sprite xe, tự động dùng hình hộp phẳng.");
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

    // ===============================================================
    // 🛠️ HÀM BỔ TRỢ 1: VẼ CHI TIẾT MỘT CỤM ĐÈN TÍN HIỆU + COUNTDOWN
    // ===============================================================
    private void drawSingleLight(GraphicsContext gc, double x, double y, double d, String state, boolean isHorizontal, int countdown) {
        double gap = Math.max(1, d / 4.0);
        Color off = Color.web("#3C3C3C"); // Màu đèn tắt

        if (isHorizontal) {
            // Vẽ hộp nền đen bọc ngoài (Ngang)
            gc.setFill(Color.web("#141414"));
            gc.fillRoundRect(x - gap, y - gap, (d + gap) * 4.0 + gap, d + (gap * 2.0), gap, gap);

            // Đổ màu 3 mắt đèn tròn
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : off); gc.fillOval(x, y, d, d);
            gc.setFill(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); gc.fillOval(x + d + gap, y, d, d);
            gc.setFill(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); gc.fillOval(x + (d + gap) * 2.0, y, d, d);

            // Ô hiển thị số Countdown điện tử cuối hộp đèn
            double boxX = x + (d + gap) * 3.0;
            gc.setFill(Color.BLACK); gc.fillRect(boxX, y, d, d);
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : (state.equalsIgnoreCase("GREEN") ? Color.GREEN : Color.YELLOW));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, Math.max(d - 1, 9)));
            gc.fillText(String.valueOf(countdown), boxX + (1 * scale), y + d - (1 * scale));
        } else {
            // Vẽ hộp nền đen bọc ngoài (Dọc)
            gc.setFill(Color.web("#141414"));
            gc.fillRoundRect(x - gap, y - gap, d + (gap * 2.0), (d + gap) * 4.0 + gap, gap, gap);

            // Ô hiển thị số Countdown điện tử đặt ở đầu hộp đèn dọc
            gc.setFill(Color.BLACK); gc.fillRect(x, y, d, d);
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : (state.equalsIgnoreCase("GREEN") ? Color.GREEN : Color.YELLOW));
            gc.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, Math.max(d - 1, 9)));
            gc.fillText(String.valueOf(countdown), x + (1 * scale), y + d - (1 * scale));

            // Đổ màu 3 mắt đèn tròn ở dưới
            gc.setFill(state.equalsIgnoreCase("RED") ? Color.RED : off); gc.fillOval(x, y + d + gap, d, d);
            gc.setFill(state.equalsIgnoreCase("YELLOW") ? Color.YELLOW : off); gc.fillOval(x, y + (d + gap) * 2.0, d, d);
            gc.setFill(state.equalsIgnoreCase("GREEN") ? Color.GREEN : off); gc.fillOval(x, y + (d + gap) * 3.0, d, d);
        }
    }

    // ===============================================================
    // 🛠️ HÀM BỔ TRỢ 2: TỰ ĐỘNG ĐỊNH VỊ VÀ CẮM ĐÈN TẠI CÁC ĐẦU NÚT GIAO LỘ
    // ===============================================================
    private void drawAllTrafficLights(GraphicsContext gc, List<Intersection> currentNodes) {
        // 🛠️ ĐOẠN ĐẾM GIÂY MOCKUP MẪU: Ông có thể thay bằng hàm lấy time thực từ engine/manager của ông nhé!
        int countdown = (int) ((20000 - (System.currentTimeMillis() % 20000)) / 1000);
        String stateEW = (System.currentTimeMillis() % 20000 < 10000) ? "GREEN" : ((System.currentTimeMillis() % 20000 < 12000) ? "YELLOW" : "RED");
        if (stateEW.equals("YELLOW")) countdown = (int) ((12000 - (System.currentTimeMillis() % 20000)) / 1000);
        else if (stateEW.equals("RED")) countdown = (int) ((20000 - (System.currentTimeMillis() % 20000)) / 1000);

        String stateNS = stateEW.equals("RED") ? "GREEN" : (stateEW.equals("GREEN") ? "RED" : "RED");
        int countdownNS = stateEW.equals("RED") ? countdown : (stateEW.equals("GREEN") ? countdown + 2 : countdown);

        // Kích thước mắt đèn co giãn động theo tầng Zoom
        double dynamicD = 11.0 * scale; if (dynamicD < 6) dynamicD = 6;
        double gap = Math.max(1, dynamicD / 4.0);
        double lightBoxLength = (dynamicD + gap) * 4.0 + gap;

        double rActual = (160.0 * scale) / 2.0; // Bán kính bùng binh xám gốc
        double currentRoadW = 160.0 * scale;
        double laneCenter = currentRoadW / 4.0;  // Tâm tịnh tiến lệch làn cắm đèn
        double paddingStop = rActual + (6.0 * scale); // Điểm lùi hộp đèn lọt ngoài bùng binh

        for (Intersection n1 : currentNodes) {
            double cx = n1.getPosition().getX() * scale + panOffsetX;
            double cy = n1.getPosition().getY() * scale + panOffsetY;

            // Quét ma trận đồ thị để kiểm tra hướng kết nối của đường sá
            boolean hasRight = false; boolean hasLeft = false; boolean hasBottom = false; boolean hasTop = false;
            for (Intersection n2 : currentNodes) {
                if (n2.getPosition().getX() > n1.getPosition().getX() + 5 && Math.abs(n2.getPosition().getY() - n1.getPosition().getY()) < 5) hasRight = true;
                if (n2.getPosition().getX() < n1.getPosition().getX() - 5 && Math.abs(n2.getPosition().getY() - n1.getPosition().getY()) < 5) hasLeft = true;
                if (n2.getPosition().getY() > n1.getPosition().getY() + 5 && Math.abs(n2.getPosition().getX() - n1.getPosition().getX()) < 5) hasBottom = true;
                if (n2.getPosition().getY() < n1.getPosition().getY() - 5 && Math.abs(n2.getPosition().getX() - n1.getPosition().getX()) < 5) hasTop = true;
            }

            // Cắm đèn rẽ nhánh tương ứng né vạch làn đường
            if (hasTop) drawSingleLight(gc, cx - laneCenter - (lightBoxLength / 2.0), cy - paddingStop - dynamicD, dynamicD, stateNS, true, countdownNS);
            if (hasBottom) drawSingleLight(gc, cx + laneCenter - (lightBoxLength / 2.0), cy + paddingStop, dynamicD, stateNS, true, countdownNS);
            if (hasLeft) drawSingleLight(gc, cx - paddingStop - dynamicD, cy - laneCenter - (lightBoxLength / 2.0), dynamicD, stateEW, false, countdown);
            if (hasRight) drawSingleLight(gc, cx + rActual + (10.0 * scale), cy + laneCenter - (lightBoxLength / 2.0), dynamicD, stateEW, false, countdown);
        }
    }

    public void render() {
        GraphicsContext gc = this.getGraphicsContext2D();
        gc.clearRect(0, 0, this.getWidth(), this.getHeight());

        double panelWidth = this.getWidth();
        double panelHeight = this.getHeight();

        // ===============================================================
        // LAYER 1: VẼ NỀN ĐẤT XANH NHẠT MƯỢT MÀ
        // ===============================================================
        gc.setFill(Color.web("#E1F0E5"));
        gc.fillRect(0, 0, panelWidth, panelHeight);

        // GIỮ NGUYÊN TỶ LỆ PHOM BÙNG BINH GỐC ĐỂ KHÔNG BỊ NUỐT NGÃ TƯ
        double circleDiameter = 160.0 * scale;
        double roadWidth = 160.0 * scale;
        Color roadColor = Color.web("#555555");

        List<Intersection> currentNodes = map.getIntersections();

        // ================= LAYER 1.1: TRẢI NHỰA ĐƯỜNG LIỀN MẠCH =================
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

        // Đổ móng vòng tròn xám bùng binh tại ngã tư
        for (Intersection node : currentNodes) {
            double cx = node.getPosition().getX() * scale + panOffsetX;
            double cy = node.getPosition().getY() * scale + panOffsetY;
            gc.setFill(roadColor);
            gc.fillOval(cx - circleDiameter / 2.0, cy - circleDiameter / 2.0, circleDiameter, circleDiameter);
        }

        // ===============================================================
        // LAYER 1.5: VẼ ĐÈ KHỐI NỀN ĐẤT CẠNH TRÒN BO GÓC KHÍT BIÊN ĐƯỜNG
        // ===============================================================
        gc.setFill(Color.web("#E1F0E5"));

        Intersection n00 = null, n01 = null, n02 = null;
        Intersection n10 = null, n11 = null, n12 = null;
        Intersection n20 = null, n21 = null, n22 = null;

        for (Intersection n : currentNodes) {
            if (n.getId().equals("Node_0_0")) n00 = n;
            if (n.getId().equals("Node_0_1")) n01 = n;
            if (n.getId().equals("Node_0_2")) n02 = n;
            if (n.getId().equals("Node_1_0")) n10 = n;
            if (n.getId().equals("Node_1_1")) n11 = n;
            if (n.getId().equals("Node_1_2")) n12 = n;
            if (n.getId().equals("Node_2_0")) n20 = n;
            if (n.getId().equals("Node_2_1")) n21 = n;
            if (n.getId().equals("Node_2_2")) n22 = n;
        }

        double rOffset = roadWidth / 2.0;
        double blockCorner = 35.0 * scale; // Cạnh tròn mềm mại lọt lòng giữa các trục lộ

        if (n00 != null && n11 != null && n22 != null) {
            double x0 = n00.getPosition().getX() * scale + panOffsetX;
            double x1 = n01.getPosition().getX() * scale + panOffsetX;
            double x2 = n02.getPosition().getX() * scale + panOffsetX;

            double y0 = n00.getPosition().getY() * scale + panOffsetY;
            double y1 = n10.getPosition().getY() * scale + panOffsetY;
            double y2 = n20.getPosition().getY() * scale + panOffsetY;

            // Ô trống 1: Trên - Trái
            double xA = x0 + rOffset; double yA = y0 + rOffset;
            double wA = (x1 - rOffset) - xA; double hA = (y1 - rOffset) - yA;
            if (wA > 0 && hA > 0) gc.fillRoundRect(xA, yA, wA, hA, blockCorner, blockCorner);

            // Ô trống 2: Trên - Phải
            double xB = x1 + rOffset; double yB = y0 + rOffset;
            double wB = (x2 - rOffset) - xB; double hB = (y1 - rOffset) - yB;
            if (wB > 0 && hB > 0) gc.fillRoundRect(xB, yB, wB, hB, blockCorner, blockCorner);

            // Ô trống 3: Dưới - Trái
            double xC = x0 + rOffset; double yC = y1 + rOffset;
            double wC = (x1 - rOffset) - xC; double hC = (y2 - rOffset) - yC;
            if (wC > 0 && hC > 0) gc.fillRoundRect(xC, yC, wC, hC, blockCorner, blockCorner);

            // Ô trống 4: Dưới - Phải
            double xD = x1 + rOffset; double yD = y1 + rOffset;
            double wD = (x2 - rOffset) - xD; double hD = (y2 - rOffset) - yD;
            if (wD > 0 && hD > 0) gc.fillRoundRect(xD, yD, wD, hD, blockCorner, blockCorner);
        }

        // ===============================================================
        // LAYER 2: VẼ VẠCH KẺ LÀN (ĐẠI LỘ MỖI BÊN CÂN ĐỐI 3 LÀN TĂM TẮP)
        // ===============================================================
        for (Road road : map.getRoads()) {
            double sx = road.getStartNode().getPosition().getX() * scale + panOffsetX;
            double sy = road.getStartNode().getPosition().getY() * scale + panOffsetY;
            double ex = road.getEndNode().getPosition().getX() * scale + panOffsetX;
            double ey = road.getEndNode().getPosition().getY() * scale + panOffsetY;

            double dx = ex - sx;
            double dy = ey - sy;
            double len = Math.sqrt(dx * dx + dy * dy);

            if (len == 0) continue;

            double ux = dx / len;
            double uy = dy / len;

            // Chặn đầu vạch khít rìa bùng binh gốc
            double rStop = circleDiameter / 2.0;
            double cutSx = sx + ux * rStop;
            double cutSy = sy + uy * rStop;
            double cutEx = ex - ux * rStop;
            double cutEy = ey - uy * rStop;

            // 1. Vẽ vạch vàng liền chính giữa tim đường đại lộ
            gc.setStroke(Color.web("#FFC107"));
            gc.setLineWidth(2.5 * scale);
            gc.strokeLine(cutSx, cutSy, cutEx, cutEy);

            // 2. Cấu hình vạch đứt trắng chia làn xe chạy
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.2 * scale);
            gc.setLineDashes(10 * scale, 10 * scale);

            // Vector pháp tuyến vuông góc trục đường để tịnh tiến dạt làn đối xứng
            double nx = -uy;
            double ny = ux;

            // Nửa mặt đường nhựa = 80px gốc. Chia 3 làn bằng nhau tuyệt đối = 26.66px
            double singleLaneW = (80.0 * scale) / 3.0;
            double lane1 = singleLaneW;
            double lane2 = singleLaneW * 2.0;

            // ---- VẼ VẠCH ĐỨT CHO BÊN PHẢI TRỤC VÀNG (Chiều đi) ----
            gc.strokeLine(cutSx + nx * lane1,   cutSy + ny * lane1,   cutEx + nx * lane1,   cutEy + ny * lane1);
            gc.strokeLine(cutSx + nx * lane2,   cutSy + ny * lane2,   cutEx + nx * lane2,   cutEy + ny * lane2);

            // ---- VẼ VẠCH ĐỨT CHO BÊN TRÁI TRỤC VÀNG (Chiều về) ----
            gc.strokeLine(cutSx - nx * lane1,   cutSy - ny * lane1,   cutEx - nx * lane1,   cutEy - ny * lane1);
            gc.strokeLine(cutSx - nx * lane2,   cutSy - ny * lane2,   cutEx - nx * lane2,   cutEy - ny * lane2);

            gc.setLineDashes((double[]) null);
        }

        // ================= LAYER 3: ĐÈ ĐẢO CỎ VÀ BO VIỀN TÂM NGÃ TƯ =================
        for (Intersection node : currentNodes) {
            double cx = node.getPosition().getX() * scale + panOffsetX;
            double cy = node.getPosition().getY() * scale + panOffsetY;

            // Đảo cỏ xanh tròn gốc thon thả (centerIslandD = 60px)
            gc.setFill(Color.web("#28A745"));
            double centerIslandD = 60.0 * scale;
            gc.fillOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);

            // Viền trắng mảnh quanh đảo cỏ cực sắc nét
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.0 * scale);
            gc.strokeOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);

            // Tên ngã tư định vị chữ bám theo bùng binh
            gc.setFill(Color.WHITE);
            gc.fillText(node.getId(), cx + (35 * scale), cy - (35 * scale));
        }

        // ===============================================================
        // 🛠️ LAYER 3.5: VẼ ĐÈ HỆ THỐNG ĐÈN GIAO THÔNG CÓ ĐẾM NGƯỢC COUNTDOWN
        // ===============================================================
        drawAllTrafficLights(gc, currentNodes);

        // ===============================================================
        // LAYER 4: VẼ XE CỘ - ĐỒNG BỘ CẤU TRÚC TRANSLATE + ROTATE GỐC
        // ===============================================================
        for (Vehicle v : vehicleManager.getVehicles()) {
            double vx = v.getPosition().getX() * scale + panOffsetX;
            double vy = v.getPosition().getY() * scale + panOffsetY;

            // Kích thước xe bốc động theo logic lõi vật lý của nhóm ông
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
            // ĐƯA TÂM VẼ VỀ CHÍNH GIỮA TỌA ĐỘ XE, SAU ĐÓ XOAY (Y hệt hàm gốc giúp nhận diện cua rẽ)
            gc.translate(vx, vy);
            gc.rotate(angle);

            if (isRectangleMode || activeSprite == null) {
                // CHẾ ĐỘ 1: Vẽ hình hộp phẳng bo góc mảnh kèm viền (Áp scale hiển thị)
                gc.setFill(rectColor);
                gc.setStroke(Color.BLACK);
                gc.setLineWidth(1.0 * scale);
                gc.fillRoundRect(-h / 2.0, -w / 2.0, h, w, 4 * scale, 4 * scale);
                gc.strokeRoundRect(-h / 2.0, -w / 2.0, h, w, 4 * scale, 4 * scale);

                // Kính chắn gió ở phần mũi xe (Mũi xe hướng về phía dương của trục X)
                gc.setFill(Color.web("#1A1C20"));
                gc.fillRoundRect(h / 2.0 - (8 * scale), -w / 2.0 + (2 * scale), 6 * scale, w - (4 * scale), 2 * scale, 2 * scale);
            } else {
                // CHẾ ĐỘ 2: IN ẢNH SPRITE THẬT LÊN CANVAS THEO TÂM XOAY CHUẨN VẬT LÝ (-h/2, -w/2)
                gc.drawImage(activeSprite, -h / 2.0, -w / 2.0, h, w);
            }

            // Đèn LED chớp nháy xe ưu tiên khẩn cấp
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

    // now
}