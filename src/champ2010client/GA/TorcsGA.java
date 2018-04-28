package champ2010client.GA;

public class TorcsGA {

    public static void main(String[] args) {

        // Create an initial population
        Population myPop = new Population(5, true);

        // Evolve our population until we reach an optimum solution
        int generationCount = 0;
//        while (myPop.getFittest().getIndividualFitness() < FitnessCalc.getMaxFitness()) {
        while (generationCount < 5){
            generationCount++;
            System.out.println("Generation: " + generationCount);
            myPop = Algorithm.evolvePopulation(myPop);
        }
        System.out.println("Solution found!");
        System.out.println("Generation: " + generationCount);
        System.out.println("Genes:");
        System.out.println(myPop.getFittest());

    }
}