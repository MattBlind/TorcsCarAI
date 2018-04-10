package champ2010client.GA;

import java.util.Random;

public class Individual {

    static int defaultGeneLength = 4;
    private double[] genes = new double[defaultGeneLength];
    // Cache
    private double fitness = 0;

    // Create a random individual
    public void generateIndividual() {
        for (int i = 0; i < size(); i++) {
            Random randInstance = new Random();
            double gene = randInstance.nextDouble();
            genes[i] = gene;
        }
    }

    /* Getters and setters */

    public double getGene(int index) {
        return genes[index];
    }

    public double[] getAllGenes() {
        return genes;
    }

    public void setGene(int index, double value) {
        genes[index] = value;
        fitness = 0;
    }

    public void setAllGenes(double value[]){
        genes = value;
        fitness = 0;
    }

    /* Public methods */
    public int size() {
        return genes.length;
    }

    public double getFitness() {
        if (fitness == 0) {
            fitness = FitnessCalc.getFitness(this);
        }
        return fitness;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += genes[i];
        }
        return geneString;
    }
}