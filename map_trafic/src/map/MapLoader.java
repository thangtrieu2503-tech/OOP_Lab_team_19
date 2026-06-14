package map;

import math.Vector2D;

public class MapLoader {
    public static RoadGraph loadMap() {
        RoadGraph graph = new RoadGraph();
        // Giờ tạo sẵn 1 cái map mẫu để chạy không bị trống
        graph.addIntersection(new Intersection(new Vector2D(100, 100), IntersectionType.FOUR_WAY));
        graph.addRoad(new Road(new Vector2D(0, 100), new Vector2D(200, 100)));
        return graph;
    }
}