package MapSystem.map;

import MapSystem.math.Vector2D;
import MapSystem.light.TrafficController;
import MapSystem.light.DelayCountdownTrafficLight;
import MapSystem.light.LightState;

public class MapLoader {

    public static RoadGraph loadMap() {
        RoadGraph graph = new RoadGraph();

        // 🛠️ CHỈNH QUY MÔ MAP Ở ĐÂY
        int rows = 3; // 3 dọc
        int cols = 3; // SỬA THÀNH 3 ngang (Map 3x3 chuẩn chỉ)

        // Tinh chỉnh lại tọa độ để 9 cái ngã tư nằm ở giữa Canvas 1090x800
        double spacing = 480.0; // Khoảng cách giữa các ngã tư
        double startX = 185.0;  // Tọa độ X ngã tư góc trên trái
        double startY = 40.0;   // Tọa độ Y ngã tư góc trên trái

        Intersection[][] grid = new Intersection[rows][cols];

        // ---------------------------------------------------------
        // BƯỚC 1: ĐÚC 9 NGÃ TƯ (TRAFFIC NODES) VÀ CẮM ĐÈN
        // ---------------------------------------------------------
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Vector2D pos = new Vector2D(startX + c * spacing, startY + r * spacing);

                // Mã Node này (Node_r_c) sẽ khớp hoàn hảo 100% với Layer 1.5 vẽ đất ở Canvas
                String nodeId = "Node_" + r + "_" + c;
                Intersection node = new Intersection(pos, nodeId);

                // =========================================================
                // 🔥 THUẬT TOÁN ĐI TÌM 4 GÓC TỰ ĐỘNG 🔥
                // =========================================================
                boolean isTopLeft = (r == 0 && c == 0);
                boolean isTopRight = (r == 0 && c == cols - 1);
                boolean isBottomLeft = (r == rows - 1 && c == 0);
                boolean isBottomRight = (r == rows - 1 && c == cols - 1);

                // Chỉ cắm đèn nếu KHÔNG PHẢI là 4 góc rẽ
                if (!isTopLeft && !isTopRight && !isBottomLeft && !isBottomRight) {
                    TrafficController boDieuKhien = new TrafficController();
                    boDieuKhien.addTrafficLight(new DelayCountdownTrafficLight(LightState.GREEN));
                    boDieuKhien.addTrafficLight(new DelayCountdownTrafficLight(LightState.RED));
                    node.setTrafficController(boDieuKhien);
                }
                // =========================================================

                grid[r][c] = node;
                graph.addIntersection(node);
            }
        }

        // ---------------------------------------------------------
        // BƯỚC 2: RẢI ĐƯỜNG BẤT ĐỘNG SẢN (ROADS & WAYS & LANES)
        // ---------------------------------------------------------
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {

                // 1. Rải đường ngang (Trái sang Phải)
                if (c < cols - 1) {
                    Intersection nodeA = grid[r][c];
                    Intersection nodeB = grid[r][c + 1];
                    // Giữ nguyên logic đường 3 làn của ông
                    graph.addRoad(new Road(nodeA, nodeB, 3));
                }

                // 2. Rải đường dọc (Trên xuống Dưới)
                if (r < rows - 1) {
                    Intersection nodeTop = grid[r][c];
                    Intersection nodeBottom = grid[r + 1][c];
                    // Giữ nguyên logic đường 3 làn của ông
                    graph.addRoad(new Road(nodeTop, nodeBottom, 3));
                }
            }
        }
        return graph;
    }
}