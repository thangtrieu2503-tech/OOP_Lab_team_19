package engine;

import java.util.Random;
import vehicle.Car;

public class VehicleSpawner {
    private Random random;

    public VehicleSpawner() { random = new Random(); }

    public Car spawnRandomCar() {
        System.out.println("Spawn random car");
        Car car = new Car();
        // Setup vị trí ngẫu nhiên cơ bản hoặc cố định ban đầu
        car.setInitialPosition(0, 100);
        return car;
    }

    public void spawnTraffic(VehicleManager manager, int amount) {
        for (int i = 0; i < amount; i++) {
            manager.addVehicle(spawnRandomCar());
        }
    }
}