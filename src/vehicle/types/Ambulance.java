package vehicle.types;

import vehicle.Vehicle;
import vehicle.behavior.EmergencyDriver;

public class Ambulance extends Vehicle {
    public Ambulance(){
        super (6.0, 45, 22, 60, new EmergencyDriver());
    }
}
