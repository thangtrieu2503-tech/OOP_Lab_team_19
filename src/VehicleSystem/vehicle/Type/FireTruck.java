package VehicleSystem.vehicle.Type;

import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.behavior.NormalDriver;
import VehicleSystem.vehicle.Vehicle;

public class FireTruck extends Vehicle {
    public FireTruck(String id, Intersection start, Intersection target, RoadGraph map) {
        super(id, "FIRE_TRUCK", start, target, 3.5, map, new NormalDriver());
    }
}