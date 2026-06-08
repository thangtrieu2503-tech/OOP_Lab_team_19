package engine;

import java.util.Random;
import vehicle.Car;

public class VehicleSpawner {

    private Random random;

    public VehicleSpawner() {

        random = new Random();
    }

    public Car spawnRandomCar() {

        System.out.println(
                "Spawn random car");

        return new Car();
    }

    public void spawnTraffic(int amount) {

        for (int i = 0;
             i < amount;
             i++) {

            spawnRandomCar();
        }
    }
}