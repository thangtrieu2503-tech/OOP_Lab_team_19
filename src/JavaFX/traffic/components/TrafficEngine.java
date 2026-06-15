package traffic.components;

import traffic.map.IntersectionNode;
import java.util.ArrayList;
import java.util.List;

public class TrafficEngine {
    private List<IntersectionNode> intersectionNodes;

    // Hệ thống đèn giao thông giả lập dùng chung cho toàn map
    private String currentMockColor = "GREEN";
    private int lightCountdown = 5; // Bắt đầu với đèn Xanh 5 giây
    private int tickCounter = 0;    // Đếm số frame để tính giây

    public TrafficEngine() {
        this.intersectionNodes = new ArrayList<>();

        // Khởi tạo sẵn bản đồ lưới 3x3 (Code cũ của bạn rất chuẩn)
        int idCounter = 1;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                this.intersectionNodes.add(new IntersectionNode(idCounter++, c, r));
            }
        }
    }

    // Nếu bạn gọi hàm này trong Thread/GameLoop (16ms/frame), nó sẽ tự chuyển đèn
    public void updateEngineTick() {
        tickCounter++;
        // Giả sử Game Loop chạy ~60 FPS (60 frame = 1 giây)
        if (tickCounter >= 60) {
            tickCounter = 0;
            lightCountdown--;

            // Hết giờ thì chuyển màu đèn
            if (lightCountdown <= 0) {
                switch (currentMockColor) {
                    case "GREEN":
                        currentMockColor = "YELLOW";
                        lightCountdown = 2; // Đèn vàng 2 giây
                        break;
                    case "YELLOW":
                        currentMockColor = "RED";
                        lightCountdown = 4; // Đèn đỏ 4 giây
                        break;
                    case "RED":
                        currentMockColor = "GREEN";
                        lightCountdown = 5; // Đèn xanh 5 giây
                        break;
                }
            }
        }
    }

    public void addCustomNode(IntersectionNode node) {
        this.intersectionNodes.add(node);
        System.out.println("🗺️ Đã mở rộng đường tới Grid(" + node.getGridX() + ", " + node.getGridY() + ")");
    }

    public List<IntersectionNode> getIntersectionNodes() {
        return this.intersectionNodes;
    }

    // --- GETTER & SETTER ĐÈN GIAO THÔNG ---
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

    // ==========================================
    // BIÊN AN TOÀN BẢN ĐỒ (GIỮ NGUYÊN TỪ CODE CŨ)
    // ==========================================
    public double getMapMinX() {
        return -500; // Cho phép xe lùi ra ngoài lề trái thoải mái để xếp hàng
    }

    public double getMapMaxX() {
        int maxGridX = intersectionNodes.stream().mapToInt(IntersectionNode::getGridX).max().orElse(2);
        return (maxGridX * 320) + 320 + 500; // Nới biên phải theo độ rộng đường mở rộng động
    }

    public double getMapMinY() {
        return -500;
    }

    public double getMapMaxY() {
        int maxGridY = intersectionNodes.stream().mapToInt(IntersectionNode::getGridY).max().orElse(2);
        return (maxGridY * 320) + 320 + 500;
    }
}