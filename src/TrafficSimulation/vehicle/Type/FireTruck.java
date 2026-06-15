package TrafficSimulation.vehicle.Type;

import TrafficSimulation.behavior.EmergencyDriver;
import TrafficSimulation.vehicle.Vehicle;
import traffic.map.IntersectionNode;
import java.util.List;

public class FireTruck extends Vehicle {
    public FireTruck(String id, IntersectionNode start, IntersectionNode target, List<IntersectionNode> fullMap) {
        super(id, "FIRE_TRUCK", start, target, 3.2, fullMap, new EmergencyDriver());
    }
}