package TrafficSimulation.vehicle.Type;

import TrafficSimulation.behavior.AggressiveDriver;
import TrafficSimulation.vehicle.Vehicle;
import traffic.map.IntersectionNode;
import java.util.List;

// Trong file Bus.java
public class Bus extends Vehicle {
    // Thay List<IntersectionNode> path thành start, target và fullMap
    public Bus(String id, IntersectionNode start, IntersectionNode target, List<IntersectionNode> fullMap) {
        super(id, "BUS", start, target, 1.5, fullMap, new AggressiveDriver());
    }
}