package champ2010client.GA;
import champ2010client.Client;
import champ2010client.Controller;

public class FitnessCalc {

    // Calculate individuals fitness by comparing it to our candidate solution
    static double getFitness(Individual individual) {
        double fitness = 0;
        // look at the individuals genes and then run the game.
        Controller driver = new MattDriverGA(individual.getAllGenes());
        Client.main(driver);
        System.out.println("running driver");
        while (fitness == 0)
            fitness = ((MattDriverGA) driver).getLastLapTime();
        System.out.println(fitness);
        return fitness;
    }

    // Get optimum fitness
    static double getMaxFitness() {
        double maxFitness = 60;
        return maxFitness;
    }
}