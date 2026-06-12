package VehicleSystem.vehicle.Type;

import VehicleSystem.behavior.EmergencyDriver;
import VehicleSystem.vehicle.Vehicle;
import traffic.map.IntersectionNode;
import java.util.List;

public class Ambulance extends Vehicle {
    public Ambulance(String id, IntersectionNode start, IntersectionNode target, List<IntersectionNode> fullMap) {
        // Tốc độ 3.5, nạp tài xế Ưu Tiên
        super(id, "AMBULANCE", start, target, 3.5, fullMap, new EmergencyDriver());
    }
}