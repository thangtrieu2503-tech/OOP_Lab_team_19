package traffic;

public class NoCountdownTrafficLight extends TrafficLight {
    public NoCountdownTrafficLight(LightState initialState, int maxTime) {
        super(initialState, maxTime);
    }

    @Override
    public void update() {
        if (timer > 0) timer--;
        else switchLight();
    }
}