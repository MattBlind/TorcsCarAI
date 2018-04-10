package champ2010client.GA;
import champ2010client.Action;
import champ2010client.Controller;
import champ2010client.SensorModel;

/**
 * Created by mattster on 06/11/17.
 */
public class MattDriverGA extends Controller {

    private double steerCoef;
    private double accel1;
    private double accel2;
    private double accel3;
    private double lastLapTime;

    public MattDriverGA(double steerCoef,double accel1, double accel2, double accel3){
        this.steerCoef = steerCoef;
        this.accel1 = accel1;
        this.accel2 = accel2;
        this.accel3 = accel3;
    }

    public MattDriverGA(double[] individualGenes){
        steerCoef = individualGenes[0];
        accel1 = individualGenes[1];
        accel2 = individualGenes[2];
        accel3 = individualGenes[3];
    }

    public Action control(SensorModel sensorsModel) {
        Action action = new Action();
        speedByTrack(sensorsModel, action);
        steering(sensorsModel,action);
        lastLapTime = sensorsModel.getLastLapTime();
        return action;
    }

    private void steering(SensorModel sensorsModel, Action action) {
        double steering = (sensorsModel.getAngleToTrackAxis() - sensorsModel.getTrackPosition()*steerCoef);
        action.steering = steering;
    }


    private void speedByTrack(SensorModel sensors, Action action) {
        double[] distSensors;
        int caseX = 0;
        distSensors = sensors.getTrackEdgeSensors();
        for(int i=8; i<11;i++){
            if (distSensors[i]<10)
                caseX = 2;
            else if (distSensors[i]<30)
                caseX = 1;
        }
        if(caseX == 0) action.accelerate = accel1;
        else if(caseX == 1) action.accelerate = accel2;
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
}
