package traffic.components;

import traffic.map.IntersectionNode;
import java.util.ArrayList;
import java.util.List;

public class TrafficEngine {
    private List<IntersectionNode> intersectionNodes;
    private List<MockVehicle> vehicleList;
    
    private String currentMockColor = "RED";
    private int lightCountdown = 3;

    public TrafficEngine() {
        this.intersectionNodes = new ArrayList<>();
        this.vehicleList = new java.util.concurrent.CopyOnWriteArrayList<>();
        
        int idCounter = 1;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                this.intersectionNodes.add(new IntersectionNode(idCounter++, c, r));
            }
        }
    }

    public void updateEngineTick() {
    }

    public void addCustomNode(IntersectionNode node) {
        this.intersectionNodes.add(node);
    }

    public List<IntersectionNode> getIntersectionNodes() {
        return this.intersectionNodes;
    }

    public List<MockVehicle> getVehicleList() {
        return this.vehicleList;
    }

    public void setVehicleList(List<MockVehicle> list) {
        this.vehicleList = list;
    }

    public String getCurrentMockColor() {
        return this.currentMockColor;
    }

    public void setCurrentMockColor(String color) {
        this.currentMockColor = color;
    }

    public int getLightCountdown() {
        return this.lightCountdown;
    }

    public void setLightCountdown(int seconds) {
        this.lightCountdown = seconds;
    }

    // NỚI RỘNG BIÊN AN TOÀN ĐỂ KHÔNG BỊ XÓA NHẦM XE KHI CHƯA CHẠY HẾT ĐƯỜNG
    public double getMapMinX() {
        return -500; // Cho phép xe lùi ra ngoài lề trái thoải mái để xếp hàng
    }
    public double getMapMaxX() {
        int maxGridX = intersectionNodes.stream().mapToInt(n -> n.getGridX()).max().orElse(2);
        return (maxGridX * 320) + 320 + 500; // Nới biên phải theo độ rộng đường mở rộng động
    }
    public double getMapMinY() {
        return -500;
    }
    public double getMapMaxY() {
        int maxGridY = intersectionNodes.stream().mapToInt(n -> n.getGridY()).max().orElse(2);
        return (maxGridY * 320) + 320 + 500;
    }
}