package ga;

import java.util.Random;
import evaluator.ServerCommunication;
import evaluator.TimeOverException;

public class SimpleGA {
	
	private double[][] population;
	private double[] popfitness;
	private int popsize;
	private int numparam;
	private Random random;
	private ServerCommunication server;
	
	static private final int simtime=600;
	static private final double pcrossover=0.6;
	static private final double mutation=0.01;
	
	
	public SimpleGA(ServerCommunication server, int numparam, int popsize){
		this.popsize = popsize;
		this.numparam = numparam;
		this.server = server;
		population = new double[popsize][numparam];
		popfitness = new double[popsize];
		random= new Random(System.currentTimeMillis());
		
		//Random Initialization
		for (int i=0; i<popsize; i++)
			for (int j=0; j<numparam; j++)
				population[i][j] = random.nextDouble();
		
	}
	
	public double[] run(int generation) throws Exception{

		double bestfit=Double.MIN_VALUE;
		double[] best = new double[numparam];
		boolean timeover= (simtime*popsize >server.getRemainingTime());
		
		for(int i=0; i<generation && !timeover; i++){
			
			//Evaluation
			double sum=0;
			double max=Double.MIN_VALUE;
			double min=Double.MAX_VALUE;
			for (int j=0; j<popsize; j++){
				Double f=null;
				try {
					f = (Double) server.launchSimulation(population[j], simtime);
				} catch (TimeOverException e) {
					//This exception is raised when simulation time is over
					//It is an alternative to the use of server.getRemainingTime()
					System.exit(-1);
				}
				popfitness[j]=f.doubleValue();
				
				//System.out.println("individual " + j + ":\t" + popfitness[j]);
				if (popfitness[j] >max)
					max=popfitness[j];
				if (popfitness[j] <min )
					min= popfitness[j];
				sum+=popfitness[j];
				
				if(popfitness[j] > bestfit){
					bestfit=popfitness[j];
					for (int k=0; k<numparam; k++)
						best[k]=population[j][k];
				}
			}
			System.out.println("#Generation " + i + " - min: " + min + " avg: " + (float)(sum/popsize) + " max:" + max);
			
			
			//Tournament Selection with tournament size = 2
			double[][] selected = new double[popsize][numparam];
			for (int j=0; j<popsize; j++){
				int a = random.nextInt(popsize);
				int b = random.nextInt(popsize-1);
				if (b>=a)
					b++;
				for (int k=0; k<numparam; k++)
					if (popfitness[a] > popfitness[b])
						selected[j][k]=population[a][k];
					else
						selected[j][k]=population[b][k];
			}				
			
			//Crossover
			double[][] recombined= new double[popsize][numparam];
			for(int j=0; (j+1)<popsize; j=j+2){
				int r= numparam;
				if (random.nextDouble()< pcrossover)
					r = random.nextInt(numparam-1)+1;
				for (int k=0; k<numparam; k++)
					if (k<r){
						recombined[j][k]=selected[j][k];
						recombined[j+1][k]=selected[j+1][k];
					}else{
						recombined[j][k]=selected[j+1][k];
						recombined[j+1][k]=selected[j][k];
					}
			}
			
			
			//Mutation
			for (int j=0; j<popsize; j++){
				for (int k=0; k<numparam; k++)
					if (random.nextDouble() < 1/numparam)
						recombined[j][k] += mutation * (random.nextDouble() - 0.5);
			}
			
			//Replacement
			population=recombined;
			
			//The best can be saved also at each generation
			//server.saveBest(best);
					
			timeover= (simtime*popsize > server.getRemainingTime());
		}
		
		if (timeover)
			System.out.println("Time Over!");
		
		//Send the best to the Server
		server.saveBest(best);
	
		String bestvalues="";
		for (int i=0; i<best.length ; i++)
			bestvalues= bestvalues + " " + best[i];
		System.out.println("Best:" + bestvalues);
		System.out.println("Fitness: " + bestfit);
		server.close();
		
		return best;
	}
	
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting...");
		ServerCommunication server = new ServerCommunication("localhost",3001);
		int n= server.getParamNumber();
		server.setFitnessFunction(new Fitness());
		
		SimpleGA ga= new SimpleGA(server,n,10);
		ga.run(20);
	}

}
