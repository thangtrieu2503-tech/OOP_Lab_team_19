package MapSystem.light;

public class CountdownTrafficLight extends TrafficLight {

    public CountdownTrafficLight(LightState initialState) {
        super(initialState);
    }

    @Override
    public String getDisplayTimer() {
        // Làm tròn lên số nguyên để UI vẽ cho đẹp (ví dụ: 14.2s thành "15")
        return String.valueOf((int) Math.ceil(internalTimer));
    }
}