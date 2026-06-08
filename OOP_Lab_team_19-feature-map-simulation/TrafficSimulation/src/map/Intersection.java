package map;

import math.Vector2D;

public class Intersection {

    private Vector2D position;
    private IntersectionType type;

    public Intersection(Vector2D position,
                        IntersectionType type) {

        this.position = position;
        this.type = type;
    }

    public Vector2D getPosition() {
        return position;
    }

    public IntersectionType getType() {
        return type;
    }
}