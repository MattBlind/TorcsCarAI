package champ2010client.GA;
import champ2010client.Client;

public class FitnessCalc {

    static double getCalcFitness(Individual individual) {
        double fitness = 0;
        int ok = 0;
        // look at the individuals genes and then run the game.
        GenAlgDriver driver = new GenAlgDriver(individual.getAllGenes());
        System.out.println("running driver");
        Client.main(driver);
        //this loop runs and waits for the driver to do a lap
        while (fitness == 0){
            fitness = driver.getLastLapTime();
            ok++;
            // retry in case of errors
            if(fitness == 0 && ok < 3){
                System.out.println("Repeat driver run");
                Client.main(driver);
            }
            // exit after multiple fails
            else if(ok > 2)
            {
                System.out.println("too many fails");
                fitness = 1000;
            }
        }

        System.out.println(fitness);
        return fitness;
    }
}