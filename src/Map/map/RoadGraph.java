package Map.map;

import Map.math.Vector2D;

import java.util.ArrayList;
import java.util.List;

public class RoadGraph {

    private List<Road> roads;
    private List<Intersection> intersections;

    public RoadGraph() {
        roads = new ArrayList<>();
        intersections = new ArrayList<>();
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    public void addIntersection(Intersection intersection) {
        intersections.add(intersection);
    }

    // ==========================================
    // NHÓM HÀM CUNG CẤP DỮ LIỆU CHO BÊN NGOÀI
    // ==========================================
    public List<Intersection> getIntersections() {
        return intersections;
    }

    public List<Road> getRoads() {
        return roads;
    }

    // ==========================================
    // BỘ NÃO TÌM ĐƯỜNG (GOOGLE MAPS) CHO XE AI
    // ==========================================
    public List<Intersection> getNeighbors(Intersection current) {
        List<Intersection> neighbors = new ArrayList<>();
        Vector2D currentPos = current.getPosition();

        // SAI SỐ CHO PHÉP (DÙNG ĐỂ CHỐNG LỖI SO SÁNH SỐ THỰC)
        // Nếu hai tọa độ lệch nhau dưới 0.1 pixel -> Coi như trùng nhau
        double epsilon = 0.1;

        for (Road road : roads) {
            Vector2D neighborPos = null;

            // SO SÁNH CÓ SAI SỐ (SỬ DỤNG HÀM GIÁ TRỊ TUYỆT ĐỐI Math.abs)
            double diffStartX = Math.abs(road.getStart().getX() - currentPos.getX());
            double diffStartY = Math.abs(road.getStart().getY() - currentPos.getY());

            double diffEndX = Math.abs(road.getEnd().getX() - currentPos.getX());
            double diffEndY = Math.abs(road.getEnd().getY() - currentPos.getY());

            if (diffStartX < epsilon && diffStartY < epsilon) {
                neighborPos = road.getEnd(); // Lấy đầu kia làm điểm đến
            } else if (diffEndX < epsilon && diffEndY < epsilon) {
                neighborPos = road.getStart(); // Lấy đầu này làm điểm đến
            }

            // Nếu mò được tọa độ của đầu kia rồi, lục tìm xem ngã tư nào đang nằm ở tọa độ đó
            if (neighborPos != null) {
                for (Intersection intersection : intersections) {

                    double diffIntersectionX = Math.abs(intersection.getPosition().getX() - neighborPos.getX());
                    double diffIntersectionY = Math.abs(intersection.getPosition().getY() - neighborPos.getY());

                    if (diffIntersectionX < epsilon && diffIntersectionY < epsilon) {
                        neighbors.add(intersection); // Bắt được hàng xóm!
                        break;
                    }
                }
            }
        }
        return neighbors;
    }

    public void printMap() {
        System.out.println("Roads: " + roads.size());
        System.out.println("Intersections: " + intersections.size());
    }
}