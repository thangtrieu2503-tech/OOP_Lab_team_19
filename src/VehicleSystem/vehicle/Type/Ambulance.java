package VehicleSystem.vehicle.Type;

import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.behavior.NormalDriver;
import VehicleSystem.vehicle.Vehicle;

public class Ambulance extends Vehicle {
    public Ambulance(String id, Intersection start, Intersection target, RoadGraph map) {
        super(id, "AMBULANCE", start, target, 4.0, map, new NormalDriver());
    }
}