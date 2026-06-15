package Map.trafficLight;

public class CountdownTrafficLight extends TrafficLight {
    public CountdownTrafficLight(LightState initialState, int maxTime) {
        super(initialState, maxTime);
    }

    @Override
    public void update() {
        if (timer > 0) timer--;
        else switchLight();
    }
}