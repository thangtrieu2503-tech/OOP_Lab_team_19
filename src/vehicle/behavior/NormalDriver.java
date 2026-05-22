package vehicle.behavior;

import vehicle.Vehicle;

public class NormalDriver implements DrivingBehavior{
    @Override
    public void handleMovement(Vehicle current, Vehicle inFront, boolean isRedLight){
        if(isRedLight){
            current.stop();
            return;
        }

        if(inFront != null){ //Nếu phỉa trước có xe
            double distance = inFront.getPositionX() - current.getPositionX();
            if(distance < current.getSafeDistance()){
                //Thử vượt nếu quá gần, nếu không thì giảm tốc
                current.overtake();
                return;
            }
        }
        //Nếu đường trống thì chạy như bình thường.
        current.move();
    }
}
