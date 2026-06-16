package MapSystem.map;

import MapSystem.math.Vector2D;

public class MapLoader {

    public static RoadGraph loadMap() {
        RoadGraph graph = new RoadGraph();

        int rows = 3;
        int cols = 3;
        double spacing = 300.0; // Thu nhỏ khoảng cách lại (Cũ là 600)
        double startX = 275.0;  // Lùi vào góc trái
        double startY = 100.0;  // Kéo lên trên

        Intersection[][] grid = new Intersection[rows][cols];

        // ---------------------------------------------------------
        // BƯỚC 1: ĐÚC 9 NGÃ TƯ (TRAFFIC NODES)
        // ---------------------------------------------------------
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Vector2D pos = new Vector2D(startX + c * spacing, startY + r * spacing);
                // Gán ID cho ngã tư để dễ debug (VD: "Node_0_0")
                Intersection node = new Intersection(pos, "Node_" + r + "_" + c);
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

                    // Constructor của Road tự động sinh ra rightWay (A->B) và leftWay (B->A)
                    // Mỗi Way sẽ tự động đẻ ra 3 Lane bên trong nhờ toán học Vector
                    Road horizontalRoad = new Road(nodeA, nodeB, 3);
                    graph.addRoad(horizontalRoad);
                }

                // 2. Rải đường dọc (Trên xuống Dưới)
                if (r < rows - 1) {
                    Intersection nodeTop = grid[r][c];
                    Intersection nodeBottom = grid[r + 1][c];

                    Road verticalRoad = new Road(nodeTop, nodeBottom, 3);
                    graph.addRoad(verticalRoad);
                }
            }
        }

        System.out.println("MapLoader ko lỗi nhé");
        return graph;
    }
}