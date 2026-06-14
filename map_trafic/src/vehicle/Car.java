package vehicle;

import math.Vector2D;

public class Car implements Vehicle {
    private double speed;
    private double maxSpeed;
    private double width;
    private Vector2D position;

    // Thuộc tính phục vụ chuyển làn và loại xe
    private double originalY;
    private double targetY;
    private boolean shiftingLane;
    private boolean emergency;

    public Car() {
        this.speed = 5.0;
        this.maxSpeed = 60.0; // Tốc độ tối đa mặc định
        this.width = 30.0;    // Chiều rộng mặc định của xe để tính khoảng cách
        this.position = new Vector2D(0, 0);
        this.originalY = 0;
        this.targetY = 0;
        this.shiftingLane = false;
        this.emergency = false;
    }

    @Override
    public void move() {
        // Logic di chuyển cơ bản, nếu đang chuyển làn thì cập nhật Y dần dần
        position.setX(position.getX() + speed);
        if (shiftingLane) {
            position.setY(targetY); // Tạm thời dịch thẳng tới làn mục tiêu
        }
        System.out.println("Car moving at X: " + position.getX() + ", Y: " + position.getY() + " with speed: " + speed);
    }

    @Override
    public void stop() { this.speed = 0; }

    @Override
    public Vector2D getPosition() { return position; }

    // Triển khai các hàm Getter/Setter cho AI
    @Override public double getX() { return position.getX(); }
    @Override public double getY() { return position.getY(); }
    @Override public double getSpeed() { return speed; }
    @Override public void setSpeed(double speed) { this.speed = speed; }
    @Override public double getMaxSpeed() { return maxSpeed; }
    @Override public double getWidth() { return width; }
    @Override public double getOriginalY() { return originalY; }
    @Override public void setTargetY(double targetY) { this.targetY = targetY; }
    @Override public boolean isShiftingLane() { return shiftingLane; }
    @Override public void setShiftingLane(boolean shifting) { this.shiftingLane = shifting; }
    @Override public boolean isEmergency() { return emergency; }

    // Thêm hàm hỗ trợ setup ban đầu khi spawn xe
    public void setInitialPosition(double x, double y) {
        this.position.setX(x);
        this.position.setY(y);
        this.originalY = y;
        this.targetY = y;
    }
    public void setEmergency(boolean emergency) { this.emergency = emergency; }
}