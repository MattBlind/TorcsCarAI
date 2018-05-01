package champ2010client.GA;

import java.util.Random;

public class Algorithm {

    /* Evolution parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.5;
    private static final double mutationOffset = 0.1;
    private static int tournamentSize = 1;
    private static final boolean elitism = true;

    /* Public methods */

    // Evolve a population
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false);
        tournamentSize = (int) (pop.size()*0.2);
        // Keep our best individual
        if (elitism) {
            newPopulation.saveIndividual(0, pop.getFittest());
        }

        // Crossover population
        int elitismStart;
        if (elitism) {
            elitismStart = 1;
        } else {
            elitismStart = 0;
        }
        // Loop over the population size and create new individuals with crossover
        for (int i = elitismStart; i < pop.size(); i++) {
            Individual indiv1 = tournamentSelection(pop);
            Individual indiv2 = tournamentSelection(pop);
            Individual newIndiv = crossover(indiv1, indiv2);
            newPopulation.saveIndividual(i, newIndiv); // only with tournament
            newPopulation.saveIndividual(i, pop.getIndividual(i));
        }
        // Mutate population
        int mutate = 0;
        for (int i = elitismStart; i < newPopulation.size(); i++) {
            if(Math.random() < mutationRate){
                mutate(newPopulation.getIndividual(i));
                mutate++;
            }
        }
        System.out.println(mutate);
        return newPopulation;
    }

    // Mutate an individual
    private static void mutate(Individual indiv) {
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            // Vary gene by mutation offset
            double randSign = 1;
            if(Math.random() <= uniformRate)
                randSign = -1;
            double gene = indiv.getGene(i) + randSign * mutationOffset;
            if (gene < 0) gene *= (-1);
            gene = Math.max (0, Math.min (1, gene)); // make sure gene is within parameters
            if(gene != 0) indiv.setGene(i, gene);
            else i--;
        }
    }

    // Select individuals for crossover
    private static Individual tournamentSelection(Population pop) {
        // Create a tournament population
        Population tournament = new Population(tournamentSize, false);
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * pop.size());
            tournament.saveIndividual(i, pop.getIndividual(randomId));
        }
        // Get the fittest
        return tournament.getFittest();
    }

    // Crossover individuals
    private static Individual crossover(Individual indiv1, Individual indiv2) {
        Individual newSol = new Individual();
        for (int i = 0; i < indiv1.size(); i++) {
            if (Math.random() <= uniformRate) {
                newSol.setGene(i, indiv1.getGene(i));
            } else {
                newSol.setGene(i, indiv2.getGene(i));
            }
        }
        return newSol;
    }
}