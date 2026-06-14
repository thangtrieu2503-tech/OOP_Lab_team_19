package engine;

import vehicle.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class VehicleManager {

    private List<Vehicle> vehicles;

    public VehicleManager() {

        vehicles = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {

        vehicles.add(vehicle);
    }

    public void updateVehicles() {

        for (Vehicle v : vehicles) {

            v.move();
        }
    }
}