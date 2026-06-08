package map;

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

    public void addIntersection(
            Intersection intersection) {

        intersections.add(intersection);
    }

    public void printMap() {

        System.out.println(
                "Roads: " + roads.size());

        System.out.println(
                "Intersections: "
                        + intersections.size());
    }
}