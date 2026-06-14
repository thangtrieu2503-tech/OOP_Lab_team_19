package traffic;

public abstract class TrafficLight {
    protected LightState state;
    protected int timer;       // Thời gian còn lại
    protected int maxTime;     // Thời gian tối đa của chu kỳ hiện tại

    public TrafficLight(LightState initialState, int maxTime) {
        this.state = initialState;
        this.maxTime = maxTime;
        this.timer = maxTime;
    }

    // Các Getter để UI hoặc Vehicle gọi lấy thông tin
    public LightState getState() { return state; }
    public int getTimer() { return timer; }

    // Logic thay đổi trạng thái (Xanh -> Vàng -> Đỏ)
    public void switchLight() {
        switch (state) {
            case GREEN:
                state = LightState.YELLOW;
                timer = 3; // Ví dụ đèn vàng luôn 3 giây
                break;
            case YELLOW:
                state = LightState.RED;
                timer = maxTime;
                break;
            case RED:
                state = LightState.GREEN;
                timer = maxTime;
                break;
        }
    }

    // Phương thức abstract để các loại đèn tự định nghĩa cách hiển thị đếm ngược
    public abstract void update();
}
