package Map.trafficLight;

public abstract class TrafficLight {
    protected LightState state;
    protected int timer;
    protected int maxTime;

    public TrafficLight(LightState initialState, int maxTime) {
        this.state = initialState;
        this.maxTime = maxTime;
        this.timer = maxTime;
    }

    public abstract void update();

    protected void switchLight() {
        switch (state) {
            case GREEN -> { state = LightState.YELLOW; timer = 3; } // Đèn vàng 3s
            case YELLOW -> { state = LightState.RED; timer = maxTime; }
            case RED -> { state = LightState.GREEN; timer = maxTime; }
        }
    }

    public LightState getState() { return state; }
    public int getTimer() { return timer; }
}