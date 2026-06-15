package VehicleSystem.vehicle.Type;

import Map.map.Intersection;
import Map.map.RoadGraph;
import VehicleSystem.behavior.NormalDriver;
import VehicleSystem.vehicle.Vehicle;

public class Car extends Vehicle {
    public Car(String id, Intersection start, Intersection target, RoadGraph map) {
        super(id, "CAR", start, target, 3.0, map, new NormalDriver());
    }
}