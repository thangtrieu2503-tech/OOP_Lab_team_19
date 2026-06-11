package TrafficSimulation.map;

public class Road {
}
package map;

import math.Vector2D;

public class Road {

    private Vector2D start;
    private Vector2D end;

    public Road(Vector2D start, Vector2D end) {

        this.start = start;
        this.end = end;
    }

    public Vector2D getStart() {

        return start;
    }

    public Vector2D getEnd() {

        return end;
    }
}