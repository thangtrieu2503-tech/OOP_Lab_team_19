package engine;

import map.*;
import math.Vector2D;
import vehicle.Car;

public class SimulationEngine {

    private VehicleManager vehicleManager;
    private VehicleSpawner spawner;
    private Camera camera;
    private RoadGraph graph;

    public SimulationEngine() {

        vehicleManager =
                new VehicleManager();

        spawner =
                new VehicleSpawner();

        camera =
                new Camera();

        graph =
                new RoadGraph();
    }

    public void start() {

        graph.addIntersection(
                new Intersection(
                        new Vector2D(0, 0),
                        IntersectionType.FOUR_WAY
                )
        );

        graph.printMap();

        Car car =
                spawner.spawnRandomCar();

        vehicleManager.addVehicle(car);

        camera.zoomIn();

        for (int i = 0; i < 5; i++) {

            vehicleManager
                    .updateVehicles();
        }
    }
}