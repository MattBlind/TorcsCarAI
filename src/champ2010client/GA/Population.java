package champ2010client.GA;


public class Population {

    private Individual[] individuals;

    /*
     *  Creates a new population and adds Individuals to it
     */
    public Population(int populationSize, boolean initialise){
        individuals = new Individual[populationSize];
        if (initialise) {
            // Loop and create individuals
            for (int i = 0; i < size(); i++) {
                Individual newIndividual = new Individual();
                newIndividual.generateIndividual();
                saveIndividual(i, newIndividual);
            }
        }
    }

    public Individual getIndividual(int index) {
        return individuals[index];
    }

    // Returns highest fitness individual
    public Individual getFittest() {
        Individual fittest = individuals[0];
        for (int i = 0; i < size(); i++)
            if (fittest.getIndividualFitness() >= getIndividual(i).getIndividualFitness())
                fittest = getIndividual(i);
        return fittest;
    }

    /* Get population size */
    public int size() {
        return individuals.length;
    }

    /* Save individual */
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }

}