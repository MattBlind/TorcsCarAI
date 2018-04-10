package champ2010client;

/**
 * Created by mattster on 06/11/17.
 */
public class MattDriverSimple extends Controller {

    double maintainSpeed = 30;
    public Action control(SensorModel sensorsModel) {

        Action action = new Action();
        if (sensorsModel.getSpeed() < maintainSpeed)
            action.accelerate = 1;
        if (sensorsModel.getAngleToTrackAxis()<0)
            action.steering = -1;
        else
            action.steering = 1;
        action.gear = 1;
        return action;
    }

    @Override
    public void reset() {
        System.out.println("Restarting.");
    }

    @Override
    public void shutdown() {
        System.out.println("Game Over.");
    }
}
