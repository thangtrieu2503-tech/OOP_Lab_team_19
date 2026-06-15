package VehicleSystem.vehicle.Type;

import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.behavior.NormalDriver;
import VehicleSystem.vehicle.Vehicle;

public class Motorbike extends Vehicle {
    public Motorbike(String id, Intersection start, Intersection target, RoadGraph map) {
        super(id, "MOTORBIKE", start, target, 3.2, map, new NormalDriver());
    }
}