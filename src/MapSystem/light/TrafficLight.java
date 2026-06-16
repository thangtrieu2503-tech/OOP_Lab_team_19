package MapSystem.light;

public abstract class TrafficLight {
    protected LightState currentState;
    protected double internalTimer; // Đếm ngược số giây (ví dụ: 15.0)
    protected double greenDuration = 15.0;
    protected double yellowDuration = 3.0;
    protected double redDuration = 15.0;

    public TrafficLight(LightState initialState) {
        this.currentState = initialState;
        resetTimerForState(initialState);
    }

    // Hàm đếm ngược thời gian chạy ngầm - Trái tim logic của đèn
    public void update(double deltaTime) {
        internalTimer -= deltaTime;
        if (internalTimer <= 0) {
            switchState();
        }
    }

    // Tự động luân chuyển trạng thái Đỏ -> Xanh -> Vàng -> Đỏ
    private void switchState() {
        switch (currentState) {
            case GREEN:
                currentState = LightState.YELLOW;
                resetTimerForState(LightState.YELLOW);
                break;
            case YELLOW:
                currentState = LightState.RED;
                resetTimerForState(LightState.RED);
                break;
            case RED:
                currentState = LightState.GREEN;
                resetTimerForState(LightState.GREEN);
                break;
        }
    }

    private void resetTimerForState(LightState state) {
        switch (state) {
            case GREEN: internalTimer = greenDuration; break;
            case YELLOW: internalTimer = yellowDuration; break;
            case RED: internalTimer = redDuration; break;
        }
    }

    // Ép chuyển màu lập tức khi người dùng Click thủ công (Manual Mode)
    public void forceSwitchState() {
        switchState();
    }

    // Getter phục vụ AI xe (Phong) và Đồ họa (Nhân)
    public LightState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(LightState state) {
        this.currentState = state;
        resetTimerForState(state);
    }

    // Hàm trừu tượng: Ép các loại đèn con tự quyết định cách hiển thị con số
    public abstract String getDisplayTimer();
}