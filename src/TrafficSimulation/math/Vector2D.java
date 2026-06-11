package math;

public class Vector2D {

    public double x;
    public double y;

    public Vector2D(double x, double y) {

        this.x = x;
        this.y = y;
    }

    public double distance(Vector2D other) {

        double dx = x - other.x;
        double dy = y - other.y;

        return Math.sqrt(dx * dx + dy * dy);
    }
}