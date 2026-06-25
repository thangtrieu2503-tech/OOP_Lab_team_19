package MapSystem.light;

import MapSystem.math.Vector2D;

public abstract class TrafficLight {
    protected LightState currentState;
    protected double internalTimer;
    protected double greenDuration = 6.0;
    protected double yellowDuration = 1.0;
    protected double redDuration = 7.0;
    private double x;
    private double y;

    public TrafficLight(LightState initialState) {
        this.currentState = initialState;
        resetTimerForState(initialState);
    }

    public void update(double deltaTime) {
        internalTimer -= deltaTime;
        if (internalTimer < 0) {
            internalTimer = 0;
        }
    }

    public void resetTimerForState(LightState state) {
        switch (state) {
            case GREEN:
                internalTimer = greenDuration;
                break;
            case YELLOW:
                internalTimer = yellowDuration;
                break;
            case RED:
                internalTimer = redDuration;
                break;
        }
    }

    public void forceSwitchState() {
        switch (currentState) {
            case GREEN:
                setCurrentState(LightState.YELLOW);
                break;
            case YELLOW:
                setCurrentState(LightState.RED);
                break;
            case RED:
                setCurrentState(LightState.GREEN);
                break;
        }
    }

    public LightState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(LightState state) {
        this.currentState = state;
        resetTimerForState(state);
    }

    public double getInternalTimer() {
        return internalTimer;
    }

    public void setGreenDuration(double greenDuration) {
        this.greenDuration = greenDuration;
    }

    public void setYellowDuration(double yellowDuration) {
        this.yellowDuration = yellowDuration;
    }

    public void setRedDuration(double redDuration) {
        this.redDuration = redDuration;
    }

    public abstract String getDisplayTimer();
    // Trong class TrafficLight
    public Vector2D getPosition() {
        return new Vector2D(this.x, this.y);
    }
}