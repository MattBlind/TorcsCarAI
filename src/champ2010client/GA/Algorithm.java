package champ2010client.GA;

import java.util.Random;

public class Algorithm {

    /* TorcsGA parameters */
    private static final double uniformRate = 0.5;
    private static final double mutationRate = 0.015;
    private static final double mutationOffset = 0.1;
    private static final int tournamentSize = 5;
    private static final boolean elitism = false;

    /* Public methods */

    // Evolve a population
    public static Population evolvePopulation(Population pop) {
        Population newPopulation = new Population(pop.size(), false);

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
//            Individual indiv1 = tournamentSelection(pop);
//            Individual indiv2 = tournamentSelection(pop);
//            Individual newIndiv = crossover(indiv1, indiv2);
//            newPopulation.saveIndividual(i, newIndiv); // only with tournament
            newPopulation.saveIndividual(i, pop.getIndividual(i));
        }
        // Mutate population
        for (int i = elitismStart; i < newPopulation.size(); i++) {
            mutate(newPopulation.getIndividual(i));
        }
        return newPopulation;
    }

    // Mutate an individual
    private static void mutate(Individual indiv) {
        double randSign = 1;
        if(Math.random()<0.5)
            randSign = -1;
        // Loop through genes
        for (int i = 0; i < indiv.size(); i++) {
            // Vary gene by mutation offset
            double gene = indiv.getGene(i) + randSign * mutationOffset;
            gene = Math.max (0, Math.min (1, gene)); // make sure gene is within parameters
            if(gene != 0) indiv.setGene(i, gene);
            else i--;
        }
        // Set new fitness
        //indiv.setNewFitness();
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
        Individual fittest = tournament.getFittest();
        return fittest;
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