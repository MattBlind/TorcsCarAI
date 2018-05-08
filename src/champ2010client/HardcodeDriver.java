package champ2010client;

/**
 * Created by mattster on 06/11/17.
 */
public class HardcodeDriver extends Controller {

    private final double accel1 = 1;
    private final double accel2 = 0.6;
    private final double accel3 = 0.9;
    private final double steerCoef = 0.7;
    private final double dist1 = 120;
    private final double dist2 = 140;
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
        action.steering = (sensorsModel.getAngleToTrackAxis() - sensorsModel.getTrackPosition()*steerCoef);
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
            if (distSensors[i]<dist1)
                caseX = 1;
            else if (distSensors[i]<dist2)
                caseX = 2;
        }
        // accelerate on open road
        if(caseX == 0) action.accelerate = accel1;
            // accelerate with turn approaching
        else if(caseX == 1) action.accelerate = accel2;
            // accelerate inside turn ?
        else if(caseX == 2) {
            if(Math.abs(sensors.getTrackPosition()) >= 1) {
                action.gear = -1;
                action.steering = -1 * action.steering /2;
                action.accelerate = accel2;
            }
            else action.accelerate = accel3;
        }

        action.gear = setGear(sensors);
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
