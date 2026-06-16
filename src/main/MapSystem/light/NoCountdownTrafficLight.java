package MapSystem.light;

public class NoCountdownTrafficLight extends TrafficLight {

    public NoCountdownTrafficLight(LightState initialState) {
        super(initialState);
    }

    @Override
    public String getDisplayTimer() {
        return ""; // Trả về chuỗi rỗng để giấu số giây đi
    }
}