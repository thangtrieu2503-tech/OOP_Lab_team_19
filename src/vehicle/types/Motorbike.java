package vehicle.types;

import vehicle.Vehicle;
import vehicle.behavior.NormalDriver;

public class Motorbike extends Vehicle {
    public Motorbike(){
        super(5.5, 25, 12, 30, new NormalDriver());
    }
}
