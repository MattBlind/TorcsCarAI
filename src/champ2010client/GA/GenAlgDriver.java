package champ2010client.GA;
import champ2010client.Action;
import champ2010client.Controller;
import champ2010client.SensorModel;

/**
 * Created by mattster on 06/11/17.
 */
public class GenAlgDriver extends Controller {

    private double steerCoef;
    private double accel1;
    private double accel2;
    private double accel3;
    private double lastLapTime;

    public Action control(SensorModel sensorsModel) {
        Action action = new Action();
        speedByTrack(sensorsModel, action);
        steering(sensorsModel,action);
        lastLapTime = sensorsModel.getLastLapTime();
        return action;
    }

    private void steering(SensorModel sensorsModel, Action action) {
        action.steering = (sensorsModel.getAngleToTrackAxis() - sensorsModel.getTrackPosition()*steerCoef);
    }


    private void speedByTrack(SensorModel sensors, Action action) {
        double[] distSensors;
        int caseX = 0;
        distSensors = sensors.getTrackEdgeSensors();
        for(int i=8; i<11;i++){
            if (distSensors[i]<40)
                caseX = 2;
            else if (distSensors[i]<90)
                caseX = 1;
        }
        // accelerate on open road
        if(caseX == 0) action.accelerate = accel1;
        // accelerate with turn approaching
        else if(caseX == 1) action.accelerate = accel2;
        // accelerate inside turn ?
        else if(caseX == 2) {
            if(Math.abs(sensors.getTrackPosition()) >= 1) {
                action.gear = -1;
                action.steering = -1 * action.steering;
                action.accelerate = accel2;
            }
            else action.accelerate = accel3;
        }

        action.gear = setGear(sensors);
    }

    private int setGear(SensorModel sensorsModel) {
        int currentGear = sensorsModel.getGear();
        if(currentGear<1) currentGear = 1;
        else if (sensorsModel.getRPM()< 3000 && currentGear != 1) currentGear--;
        else if(sensorsModel.getRPM() > 7000) currentGear++;
        return currentGear;
    }

    public void setParameters(double[] paramSet){
        steerCoef = paramSet[0];
        accel1 = paramSet[1];
        accel2 = paramSet[2];
        accel3 = paramSet[3];
    }

    public double getLastLapTime(){
        return lastLapTime;
    }

    @Override
    public void reset() {
        System.out.println("Restarting Race.");
    }

    @Override
    public void shutdown() {
        System.out.println("Game Over.");
    }
}
