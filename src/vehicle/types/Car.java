package vehicle.types;

import vehicle.Vehicle;
import vehicle.behavior.NormalDriver;

public class Car extends Vehicle {
    public Car(){
        super(4.0, 40, 20, 50, new NormalDriver());
    }
}

