package MapSystem.map;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RoadGraph {
    private List<Intersection> intersections;
    private List<Road> roads;

    public RoadGraph() {
        intersections = new ArrayList<>();
        roads = new ArrayList<>();
    }

    public void addIntersection(Intersection node) {
        intersections.add(node);
    }

    public void addRoad(Road road) {
        roads.add(road);
    }

    // 💡 TÍNH TOÁN DANH SÁCH "HÀNG XÓM" THỜI GIAN THỰC
    // Mỗi khi xe hỏi đường, ta mới quét danh sách roads để trả lời.
    // Cách này đảm bảo xe LUÔN CẬP NHẬT theo đúng những gì ông vừa vẽ!
    public List<Intersection> getNeighbors(Intersection node) {
        List<Intersection> neighbors = new ArrayList<>();
        for (Road road : roads) {
            if (road.getStartNode().equals(node)) {
                neighbors.add(road.getEndNode());
            } else if (road.getEndNode().equals(node)) {
                neighbors.add(road.getStartNode());
            }
        }
        return neighbors;
    }

    public List<Intersection> getIntersections() { return intersections; }
    public List<Road> getRoads() { return roads; }
}