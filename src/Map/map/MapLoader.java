package Map.map;

import Map.math.Vector2D;

public class MapLoader {
    public static RoadGraph loadMap() {
        RoadGraph graph = new RoadGraph();

        // Xây dựng thành phố 3x3 ngã tư bằng tọa độ thực
        int rows = 3;
        int cols = 3;
        double spacing = 320.0; // Khoảng cách giữa các ngã tư
        double startX = 160.0;  // Tọa độ bắt đầu
        double startY = 160.0;

        Intersection[][] grid = new Intersection[rows][cols];

        // ==========================================
        // 1. TRỒNG CÁC BÙNG BINH NGÃ TƯ
        // ==========================================
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Vector2D pos = new Vector2D(startX + c * spacing, startY + r * spacing);

                // Lưu ý: Nếu IntersectionType báo đỏ, ông cứ truyền null vào cũng không sao
                Intersection intersection = new Intersection(pos, null);

                grid[r][c] = intersection;
                graph.addIntersection(intersection);
            }
        }

        // ==========================================
        // 2. TRÁNG NHỰA ĐƯỜNG NỐI CÁC NGÃ TƯ LẠI VỚI NHAU
        // ==========================================
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Rải đường nằm NGANG (Nối sang phải)
                if (c < cols - 1) {
                    Road horizontalRoad = new Road(grid[r][c].getPosition(), grid[r][c+1].getPosition());
                    graph.addRoad(horizontalRoad);
                }
                // Rải đường nằm DỌC (Nối xuống dưới)
                if (r < rows - 1) {
                    Road verticalRoad = new Road(grid[r][c].getPosition(), grid[r+1][c].getPosition());
                    graph.addRoad(verticalRoad);
                }
            }
        }

        System.out.println("✅ Đã load thành công bản đồ lưới 3x3 xịn sò!");
        return graph;
    }
}