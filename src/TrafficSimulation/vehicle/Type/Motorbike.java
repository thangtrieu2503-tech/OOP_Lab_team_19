package TrafficSimulation.vehicle.Type;

import TrafficSimulation.behavior.NormalDriver;
import TrafficSimulation.vehicle.Vehicle;
import traffic.map.IntersectionNode;
import java.util.List;

public class Motorbike extends Vehicle {
    public Motorbike(String id, IntersectionNode start, IntersectionNode target, List<IntersectionNode> fullMap) {
        super(id, "MOTORBIKE", start, target, 2.8,fullMap, new NormalDriver());
    }
}