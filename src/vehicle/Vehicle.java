package vehicle;

import vehicle.behavior.DrivingBehavior;

public abstract class Vehicle {
    protected double speed;
    protected double maxSpeed;
    protected double positionX;
    protected double positionY;
    protected double width;
    protected double height;
    protected double safeDistance;

    protected DrivingBehavior drivingBehavior;
    protected MovementState currentState;

    public Vehicle(double maxSpeed, double width, double height, double safeDistance, DrivingBehavior drivingBehavior){
        this.maxSpeed = maxSpeed;
        this.width = width;
        this.height = height;
        this.safeDistance = safeDistance;
        this.drivingBehavior = drivingBehavior;
        this.currentState = MovementState.GOING_STRAIGHT;
    }

    //Các hành vi cốt lõi:
    public void move(){
        this.speed = maxSpeed;
        //Cập nhật vị trí
    }

    public void stop(){
        this.speed = 0;
    }

    public void overtake(){
        this.currentState = MovementState.OVERTAKING;
        //Logic vượt xe
    }

    public void turnleft(){
        this.currentState = MovementState.TURNING_LEFT;

    }

    public void turnright(){
        this.currentState = MovementState.TURNING_RIGHT;
    }

    public void playSound(String soundFile){

    }

    public void updateStatus(Vehicle vehicleInfront, boolean isRedLight){
        drivingBehavior.handleMovement(this, vehicleInfront, isRedLight);
    }

    //Getter + Setter
    public double getPositionX() {
        return positionX;
    }
    public double getPositionY() {
        return positionY;
    }
    public double getWidth() {
        return width;
    }
    public double getHeight() {
        return height;
    }
    public double getSafeDistance() {
        return safeDistance;
    }
    public MovementState getCurrentState() {
        return currentState;
    }
    public void setPosition(double x, double y){
        this.positionX = x;
        this.positionY = y;
    }
    public void setDrivingBehavior(DrivingBehavior behavior){
        this.drivingBehavior = behavior;
    }
}
