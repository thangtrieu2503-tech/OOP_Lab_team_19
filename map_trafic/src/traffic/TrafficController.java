package traffic;

import java.util.ArrayList;
import java.util.List;

public class TrafficController {
    private List<TrafficLight> lights;

    public TrafficController() {
        lights = new ArrayList<>();
    }

    public void addLight(TrafficLight light) { lights.add(light); }

    public void updateLights() {
        for (TrafficLight light : lights) {
            light.update();
        }
    }

    public List<TrafficLight> getLights() { return lights; }
}