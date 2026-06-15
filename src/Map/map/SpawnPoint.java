package Map.map;

import Map.math.Vector2D;

public class SpawnPoint {
    private Vector2D position;
    private int trafficRate;

    public SpawnPoint(Vector2D position, int trafficRate) {
        this.position = position;
        this.trafficRate = trafficRate;
    }

    public Vector2D getPosition() { return position; }
    public int getTrafficRate() { return trafficRate; }
}