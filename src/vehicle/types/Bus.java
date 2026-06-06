package vehicle.types;

import vehicle.Vehicle;
import vehicle.behavior.AggressiveDriver;
import vehicle.behavior.EmergencyDriver;

public class Bus extends Vehicle {
    public Bus() {
        super(6.0, 45, 22, 60, new AggressiveDriver());
    }
}
