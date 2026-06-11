package traffic;

public class DelayCountdownTrafficLight extends TrafficLight {

    public DelayCountdownTrafficLight(LightState initialState, int maxTime) {
        super(initialState, maxTime);
    }

    @Override
    public void update() {
        if (timer > 0) {
            timer--;
        } else {
            switchLight(); // Hết giờ thì tự động đổi trạng thái đèn
        }
    }

    // UI sẽ gọi hàm này thay vì getTimer() gốc
    public String getDisplayTimer() {
        if (timer <= 10) {
            return String.valueOf(timer); // Nếu thời gian <= 10, hiển thị số giây
        }
        return ""; // Nếu lớn hơn 10s thì giấu đi (trả về chuỗi rỗng)
    }
}