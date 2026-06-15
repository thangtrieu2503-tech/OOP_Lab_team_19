package TrafficSimulation.vehicle.Type; // (Đổi lại tên package nếu máy ông đang dùng VehicleSystem.vehicle)

import TrafficSimulation.behavior.NormalDriver;
import TrafficSimulation.vehicle.Vehicle;
import traffic.map.IntersectionNode;
import java.util.List;

public class Car extends Vehicle {
    public Car(String id, IntersectionNode start, IntersectionNode target, List<IntersectionNode> fullMap) {
        // Gọi super() truyền lên Vehicle cha: id, type("CAR"), x, y, tốc độ(2.5), lộ trình, tài xế thường
        super(id, "CAR", start, target, 2.5, fullMap, new NormalDriver());
    }
}