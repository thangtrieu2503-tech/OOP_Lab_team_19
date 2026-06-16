package UI;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import MapSystem.map.Intersection;
import MapSystem.map.Road;
import MapSystem.map.RoadGraph;
import VehicleSystem.vehicle.Vehicle;
import VehicleSystem.vehicle.VehicleManager;
import VehicleSystem.vehicle.type.*;

public class SimulationCanvas extends Canvas {

    private RoadGraph map;
    private VehicleManager vehicleManager;

    public SimulationCanvas(double width, double height, RoadGraph map, VehicleManager vehicleManager) {
        super(width, height);
        this.map = map;
        this.vehicleManager = vehicleManager;
        // Đã tháo toàn bộ sự kiện MouseDrag và Scroll, bản đồ giờ khóa cứng!
    }

    public void render() {
        GraphicsContext gc = this.getGraphicsContext2D();

        // 1. Nền sa hình
        gc.setFill(Color.web("#D4EDDA"));
        gc.fillRect(0, 0, getWidth(), getHeight());

        // CHUẨN TỶ LỆ: Đường rộng 160px để ôm trọn 3 làn xe mỗi bên (mỗi làn 25px + phân cách)
        double roadWidth = 160.0;

        // ===============================================================
        // VẼ ĐƯỜNG ĐẠI LỘ NHỰA VÀ VẠCH KẺ LÀN
        // ===============================================================
        for (Road road : map.getRoads()) {
            double sx = road.getStartNode().getPosition().getX();
            double sy = road.getStartNode().getPosition().getY();
            double ex = road.getEndNode().getPosition().getX();
            double ey = road.getEndNode().getPosition().getY();

            // Nhựa đường
            gc.setStroke(Color.web("#555555"));
            gc.setLineWidth(roadWidth);
            gc.setLineCap(StrokeLineCap.BUTT);
            gc.strokeLine(sx, sy, ex, ey);

            // Vạch vàng tâm đường
            gc.setStroke(Color.web("#FFC107"));
            gc.setLineWidth(3.0);
            gc.strokeLine(sx, sy, ex, ey);

            // Vạch nét đứt phân làn
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(1.5);
            gc.setLineDashes(10, 10);

            for (Road.Lane lane : road.getLanes()) {
                gc.strokeLine(lane.startPos.getX(), lane.startPos.getY(),
                        lane.endPos.getX(), lane.endPos.getY());
            }
            gc.setLineDashes((double[]) null);
        }

        // ===============================================================
        // VẼ BÙNG BINH & ĐẢO CỎ TẠI NGÃ TƯ
        // ===============================================================
        double circleDiameter = 160.0; // Bằng đúng chiều rộng đường để không bị lồi ra ngoài
        double centerIslandD = 60.0;

        for (Intersection node : map.getIntersections()) {
            double cx = node.getPosition().getX();
            double cy = node.getPosition().getY();

            // Nền bùng binh
            gc.setFill(Color.web("#555555"));
            gc.fillOval(cx - circleDiameter / 2.0, cy - circleDiameter / 2.0, circleDiameter, circleDiameter);

            // Đảo cỏ
            gc.setFill(Color.web("#28A745"));
            gc.fillOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);

            // Viền đảo cỏ
            gc.setStroke(Color.WHITE);
            gc.setLineWidth(2.0);
            gc.strokeOval(cx - centerIslandD / 2.0, cy - centerIslandD / 2.0, centerIslandD, centerIslandD);

            // Tên ngã tư
            gc.setFill(Color.WHITE);
            gc.fillText(node.getId(), cx + 35, cy - 35);
        }

        // ===============================================================
        // VẼ XE CỘ TRÊN ĐƯỜNG
        // ===============================================================
        for (Vehicle v : vehicleManager.getVehicles()) {
            double vx = v.getPosition().getX();
            double vy = v.getPosition().getY();
            double w = v.getWidth();
            double h = v.getLength();

            Color rectColor = Color.DODGERBLUE;
            if (v instanceof Ambulance) rectColor = Color.WHITE;
            else if (v instanceof FireTruck) rectColor = Color.CRIMSON;
            else if (v instanceof Bus) rectColor = Color.DARKBLUE;
            else if (v instanceof Motorbike) rectColor = Color.ORANGE;
            else rectColor = Color.DARKSLATEGRAY;

            // Gọi thẳng cái góc chuẩn xác 100% từ lõi vật lý
            double angle = v.getAngle();

            gc.save();
            // Đưa tâm vẽ về chính giữa tọa độ xe, sau đó xoay
            gc.translate(vx, vy);
            gc.rotate(angle);

            // Vẽ thân xe (Tâm ở 0,0 do đã translate)
            gc.setFill(rectColor);
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1.0);
            gc.fillRoundRect(-h / 2.0, -w / 2.0, h, w, 4, 4);
            gc.strokeRoundRect(-h / 2.0, -w / 2.0, h, w, 4, 4);

            // Kính chắn gió ở phần mũi xe (Mũi xe hướng về phía dương của trục X sau khi rotate)
            gc.setFill(Color.web("#1A1C20"));
            gc.fillRoundRect(h / 2.0 - 8, -w / 2.0 + 2, 6, w - 4, 2, 2);

            // Đèn LED chớp nháy xe ưu tiên
            if (v instanceof Ambulance || v instanceof FireTruck) {
                boolean toggle = (System.currentTimeMillis() / 150) % 2 == 0;
                double ledSize = 4.0;

                gc.setFill(toggle ? Color.RED : Color.BLUE);
                gc.fillOval(-h/4.0, -w/2.0 + 1, ledSize, ledSize);

                gc.setFill(toggle ? Color.BLUE : Color.RED);
                gc.fillOval(-h/4.0, w/2.0 - ledSize - 1, ledSize, ledSize);
            }

            gc.restore();
        }
    }
}