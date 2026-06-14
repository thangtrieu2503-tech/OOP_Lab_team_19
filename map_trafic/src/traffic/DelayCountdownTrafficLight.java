package traffic;

public class DelayCountdownTrafficLight extends TrafficLight {
    public DelayCountdownTrafficLight(LightState initialState, int maxTime) {
        super(initialState, maxTime);
    }

    @Override
    public void update() {
        if (timer > 0) timer--;
        else switchLight();
    }

    public String getDisplayTimer() {
        return (timer <= 10) ? String.valueOf(timer) : "";
    }
}