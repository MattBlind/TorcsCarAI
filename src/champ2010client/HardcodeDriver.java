package champ2010client;

/**
 * Created by mattster on 06/11/17.
 */
public class HardcodeDriver extends Controller {

    private double lastLapTime;

    public Action control(SensorModel sensorsModel) {

        Action action = new Action();
        action.gear = setGear(sensorsModel);
        speedByTrack(sensorsModel, action);
        steering(sensorsModel,action);
        lastLapTime = sensorsModel.getLastLapTime();
        return action;
    }

    private void steering(SensorModel sensorsModel, Action action) {
        action.steering = (sensorsModel.getAngleToTrackAxis() - sensorsModel.getTrackPosition()*0.6);
    }

    private int setGear(SensorModel sensorsModel) {
        int currentGear = sensorsModel.getGear();
        if(currentGear<1) currentGear = 1;
        else if (sensorsModel.getRPM()< 3000 && currentGear != 1) currentGear--;
                else if(sensorsModel.getRPM() > 7000) currentGear++;
        return currentGear;
    }

    private void speedByTrack(SensorModel sensors, Action action) {
        double[] distSensors;
        int caseX = 0;
        distSensors = sensors.getTrackEdgeSensors();
        for(int i=8; i<11;i++){
            if (distSensors[i]<40)
                caseX = 2;
            else if (distSensors[i]<100)
                    caseX = 1;
        }
        if(caseX == 0) action.accelerate = 1;
            else if(caseX == 1) action.accelerate = 0.5;
            else if(caseX == 2) action.accelerate = 0.2;
    }

    public double getLastLapTime(){
        return lastLapTime;
    }

    @Override
    public void reset() {
        System.out.println("Restarting.");
    }

    @Override
    public void shutdown() {
        System.out.println("Game Over.");
    }

    @Override
    public void setParameters(double[] paramSet) { System.out.println("Not needed for this model"); }
}
