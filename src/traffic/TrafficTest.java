package traffic;

public class TrafficTest {
    public static void main(String[] args) {
        // Tạo một bộ điều khiển
        TrafficController controller = new TrafficController();

        // Tạo 1 đèn đếm ngược, chu kỳ 15 giây, màu ban đầu là ĐỎ
        TrafficLight light1 = new CountdownTrafficLight(LightState.RED, 15);
        controller.addTrafficLight(light1);

        // Giả lập hệ thống thời gian trôi qua (System Timer)
        for (int i = 0; i < 20; i++) {
            System.out.println("Giây thứ " + i + ": Đèn đang màu " + light1.getState() + " - Còn: " + light1.getTimer() + "s");
            controller.update(); // Gọi update để giảm thời gian
        }
    }
}
