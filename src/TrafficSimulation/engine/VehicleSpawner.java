package engine;

import vehicle.Car;

import java.util.Random;

public class VehicleSpawner {

    private Random random;

    public VehicleSpawner() {

        random = new Random();
    }

    public Car spawnRandomCar() {

        double randomSpeed = 2 + random.nextInt(8);

        System.out.println("Spawned car speed: " + randomSpeed);

        return new Car();
    }

    public void spawnTraffic(int amount) {

        for (int i = 0; i < amount; i++) {

            spawnRandomCar();
        }
    }
}