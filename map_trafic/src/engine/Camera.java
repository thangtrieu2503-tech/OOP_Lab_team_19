package engine;

public class Camera {
    private double scale;
    public Camera() { scale = 1.0; }
    public void zoomIn() { scale += 0.1; }
    public void zoomOut() { scale -= 0.1; }
    public double getScale() { return scale; }
}