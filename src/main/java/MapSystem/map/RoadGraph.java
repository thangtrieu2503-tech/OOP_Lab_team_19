package MapSystem.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadGraph {
    private List<Intersection> intersections;
    private List<Road> roads;

    // Cuốn sổ tay chỉ đường: Ngã tư A nối với những ngã tư nào?
    private Map<Intersection, List<Intersection>> adjacencyList;

    public RoadGraph() {
        intersections = new ArrayList<>();
        roads = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    public void addIntersection(Intersection node) {
        intersections.add(node);
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void addRoad(Road road) {
        // 1. Lưu con đường này vào danh sách tổng để quản lý/vẽ đồ họa
        roads.add(road);

        // 2. CẬP NHẬT ĐỒ THỊ 2 CHIỀU CHO NÃO XE TÌM ĐƯỜNG:
        // Thêm kết nối chiều đi (Từ Start -> End)
        adjacencyList.get(road.getStartNode()).add(road.getEndNode());

        // Thêm kết nối chiều về (Từ End -> Start) để xe không bị kẹt một chiều
        adjacencyList.get(road.getEndNode()).add(road.getStartNode());
    }

    public List<Intersection> getNeighbors(Intersection node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public List<Intersection> getIntersections() { return intersections; }
    public List<Road> getRoads() { return roads; }
}