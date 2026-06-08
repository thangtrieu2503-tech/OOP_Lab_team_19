package traffic.components;

import traffic.sounds.SoundManager;

public class MockVehicle {
    private String type;
    private double x;
    private double y;
    private int speed;
    private double angle;
    private boolean honking = false;

    public MockVehicle(String type, double x, double y, int speed, double angle) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.angle = angle;
    }

    public String getType() { return type; }
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }
    public boolean isHonking() { return honking; }

    public void setHonking(boolean honking) {
        if (honking && !this.honking) {
            switch (this.type) {
                case "CAR":
                    traffic.sounds.SoundManager.play("CAR_HORN");
                    break;
                case "BIKE":
                case "MOTORBIKE":
                    traffic.sounds.SoundManager.play("MOTORBIKE_HORN");
                    break;
                case "BUS":
                    traffic.sounds.SoundManager.play("BUS_HORN");
                    break;
                case "AMBULANCE":
                    traffic.sounds.SoundManager.play("AMBULANCE_SIREN");
                    break;
                case "FIRE_TRUCK":
                    traffic.sounds.SoundManager.play("FIRE_TRUCK_SIREN");
                    break;
                default:
                    traffic.sounds.SoundManager.play("HORN");
                    break;
            }
        }
        this.honking = honking;
    }
}