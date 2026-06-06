package vehicle.types;

import vehicle.Vehicle;
import vehicle.behavior.EmergencyDriver;

public class FireTruck extends Vehicle {
    public FireTruck(){
        super(5.0, 60, 25, 70, new EmergencyDriver());
    }
}
