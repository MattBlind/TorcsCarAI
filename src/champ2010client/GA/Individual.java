package champ2010client.GA;

import java.util.Random;

public class Individual {

    private static int defaultGeneLength = 6;
    private double[] genes = new double[defaultGeneLength];
    // Cache
    private double fitness = 0;

    // Create a random individual
    public void generateIndividual() {
        for (int i = 0; i < size(); i++) {
            Random randInstance = new Random();
            double gene = randInstance.nextDouble();
            genes[i] = gene;
            if(i>3) genes[i] *= 200;
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

    /* Public methods */
    public int size() {
        return genes.length;
    }

    public double getIndividualFitness() {
        return fitness;
    }

    public void setNewFitness(double newFitness){
        fitness = newFitness;
    }

    @Override
    public String toString() {
        String geneString = "";
        for (int i = 0; i < size(); i++) {
            geneString += genes[i]+" ";
        }
        return geneString;
    }
}
