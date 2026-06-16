package MapSystem.light;

public class DelayCountdownTrafficLight extends TrafficLight {

    public DelayCountdownTrafficLight(LightState initialState) {
        super(initialState);
    }

    @Override
    public String getDisplayTimer() {
        if (internalTimer <= 10.0) {
            return String.valueOf((int) Math.ceil(internalTimer));
        }
        return ""; // Nếu còn nhiều thời gian thì giấu số giây đi
    }
}