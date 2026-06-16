package MapSystem.map;

import MapSystem.math.Vector2D;
import MapSystem.light.TrafficController;

public class Intersection {
    private Vector2D position;
    private String id;
    private TrafficController trafficController; // Tích hợp sẵn chỗ cắm đèn giao thông

    public Intersection(Vector2D position, String id) {
        this.position = position;
        this.id = id;
    }

    public Vector2D getPosition() { return position; }
    public String getId() { return id; }

    public TrafficController getTrafficController() { return trafficController; }
    public void setTrafficController(TrafficController controller) { this.trafficController = controller; }
}