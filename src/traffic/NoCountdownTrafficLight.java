package traffic;

public class NoCountdownTrafficLight extends TrafficLight {

    public NoCountdownTrafficLight(LightState initialState, int maxTime) {
        // Gọi hàm khởi tạo của lớp cha (TrafficLight)
        super(initialState, maxTime);
    }

    @Override
    public void update() {
        if (timer > 0) {
            timer--; // Vẫn ngầm trừ thời gian mỗi tick
        } else {
            switchLight(); // Hết giờ thì tự động đổi trạng thái đèn
        }
    }

    // Hàm này dành riêng cho UI gọi để lấy text hiển thị
    public String getDisplayTimer() {
        return ""; // Trả về chuỗi rỗng vì loại này không hiển thị số
    }
}
