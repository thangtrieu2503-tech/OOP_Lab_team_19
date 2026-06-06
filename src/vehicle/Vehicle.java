package vehicle;

import vehicle.behavior.DrivingStrategy;
import java.util.List;

public abstract class Vehicle {
    protected double speed;
    protected double maxSpeed;
    protected double positionX;
    protected double positionY;
    protected double width;
    protected double height;
    protected double safeDistance;

    protected DrivingStrategy drivingStrategy;
    protected MovementState currentState;

    public Vehicle(double maxSpeed, double width, double height, double safeDistance, DrivingStrategy drivingStrategy){
        this.maxSpeed = maxSpeed;
        this.width = width;
        this.height = height;
        this.safeDistance = safeDistance;
        this.drivingStrategy = drivingStrategy;
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

    public void updateStatus(Vehicle vehicleInfront, boolean isRedLight, List<Vehicle> allVehicles){
        drivingStrategy.updateMovement(this, vehicleInfront, isRedLight, 2.5, allVehicles);
    }

    //Getter + Setter

    public double getSpeed() {
        return speed;
    }

    public double getMaxSpeed() {
        return maxSpeed;
    }

    public double getX() {
        return positionX;
    }
    public double getY() {
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
    public void setDrivingBehavior(DrivingStrategy behavior){
        this.drivingStrategy = behavior;
    }
}
