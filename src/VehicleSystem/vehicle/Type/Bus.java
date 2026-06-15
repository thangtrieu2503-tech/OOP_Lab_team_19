package VehicleSystem.vehicle.Type;

import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.behavior.NormalDriver; // Nếu ông có BusDriver thì thay vào đây nhé
import VehicleSystem.vehicle.Vehicle;

public class Bus extends Vehicle {
    public Bus(String id, Intersection start, Intersection target, RoadGraph map) {
        super(id, "BUS", start, target, 2.0, map, new NormalDriver());
    }
}