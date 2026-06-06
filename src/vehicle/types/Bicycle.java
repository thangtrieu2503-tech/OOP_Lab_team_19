package vehicle.types;

import vehicle.Vehicle;
import vehicle.behavior.NormalDriver;

public class Bicycle extends Vehicle {
    public Bicycle(){
        super(1.5, 20, 8, 15, new NormalDriver());
    }
}
