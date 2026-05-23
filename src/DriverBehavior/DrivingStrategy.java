package DriverBehavior;

import VehicleSystem.Vehicle;
import java.util.List;

/**
 * Giao diện chung cho tất cả các phong cách lái xe (AI xe).
 */
public interface DrivingStrategy {
    /**
     * Cập nhật trạng thái di chuyển của xe dựa trên môi trường xung quanh.
     */
    void updateMovement(Vehicle current, Vehicle frontVehicle, boolean isRedLight, double distanceToLight, List<Vehicle> allVehicles);
}