package champ2010client;

/**
 * Created by mattster on 06/11/17.
 */
public class MattDriverSimple extends Controller {

    @Override
    public Action control(SensorModel sensors) {


        return null;
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
