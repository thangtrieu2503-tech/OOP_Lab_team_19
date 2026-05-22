package vehicle.behavior;

import vehicle.Vehicle;
//Các bố xe ưu tiên thì còi phải to, vượt đèn đỏ,...
public class EmergencyDriver implements DrivingBehavior{
    @Override
    public void handleMovement(Vehicle current, Vehicle inFront, boolean isRedLight){
        //Các bố đếch quan tâm đèn đỏ, đến đoạn vượt xe luôn
        if(inFront != null){
            double distance = inFront.getPositionX() - current.getPositionX();
            if(distance < current.getSafeDistance()){
                current.playSound("soundfile");
                current.overtake();
            }
        }
        current.move();
    }
}
