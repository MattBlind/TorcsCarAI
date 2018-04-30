package evaluator;

public class DefaultFitness implements FitnessFunction{

	public Double getFitness(double bestlap, double topspeed, double distraced,double damage) {
		return new Double(topspeed);
	}

}
