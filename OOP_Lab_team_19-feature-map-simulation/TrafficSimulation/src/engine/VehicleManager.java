package engine;

import java.util.ArrayList;
import java.util.List;
import vehicle.Vehicle;

public class VehicleManager {

    private List<Vehicle> vehicles;

    public VehicleManager() {

        vehicles = new ArrayList<>();
    }

    public void addVehicle(Vehicle vehicle) {

        vehicles.add(vehicle);
    }

    public void updateVehicles() {

        for (Vehicle vehicle : vehicles) {

            vehicle.move();
        }
    }
}