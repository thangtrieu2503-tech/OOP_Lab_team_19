package MapSystem.light;

public class DelayCountdownTrafficLight extends TrafficLight {

    public DelayCountdownTrafficLight(LightState initialState) {
        super(initialState);
    }

    @Override
    public String getDisplayTimer() {
        if (internalTimer > 0) {
            return String.valueOf((int) Math.ceil(internalTimer));
        }
        return "0";
    }
}
