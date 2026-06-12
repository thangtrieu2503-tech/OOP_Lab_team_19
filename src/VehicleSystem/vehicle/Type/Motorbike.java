package VehicleSystem.vehicle.Type;

import VehicleSystem.behavior.NormalDriver;
import VehicleSystem.vehicle.Vehicle;
import traffic.map.IntersectionNode;
import java.util.List;

public class Motorbike extends Vehicle {
    public Motorbike(String id, IntersectionNode start, IntersectionNode target, List<IntersectionNode> fullMap) {
        super(id, "MOTORBIKE", start, target, 2.8,fullMap, new NormalDriver());
    }
}