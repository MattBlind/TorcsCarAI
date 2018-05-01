/**
 *
 */
package champ2010client;

import champ2010client.Action;
import champ2010client.Controller;
import champ2010client.Controller.Stage;
import champ2010client.GA.Algorithm;
import champ2010client.GA.Population;
import champ2010client.GA.Stats;
import champ2010client.MessageBasedSensorModel;
import champ2010client.SocketHandler;

import java.util.StringTokenizer;

/**
 * @author Daniele Loiacono
 *
 */
public class Client {

	private static int UDP_TIMEOUT = 1000;
	private static int port;
	private static String host;
	private static String clientId;
	private static boolean verbose;
	private static int maxEpisodes;
	private static int maxSteps;
	private static Stage stage;
	private static String trackName;

	/**
	 * @param args
	 *            is used to define all the options of the client.
	 *            <port:N> is used to specify the port for the connection (default is 3001)
	 *            <host:ADDRESS> is used to specify the address of the host where the server is running (default is localhost)
	 *            <id:ClientID> is used to specify the ID of the client sent to the server (default is championship2009)
	 *            <verbose:on> is used to set verbose mode on (default is off)
	 *            <maxEpisodes:N> is used to set the number of episodes (default is 1)
	 *            <maxSteps:N> is used to set the max number of steps for each episode (0 is default value, that means unlimited number of steps)
	 *            <stage:N> is used to set the current stage: 0 is WARMUP, 1 is QUALIFYING, 2 is RACE, others value means UNKNOWN (default is UNKNOWN)
	 *            <trackName:name> is used to set the name of current track
	 */
	public static void main(String[] args) {
		parseParameters(args);
		SocketHandler mySocket = new SocketHandler(host, port, verbose);
		String inMsg;

		Controller driver = load(args[0]);
		driver.setStage(stage);
		driver.setTrackName(trackName);

		/* Build init string */
		float[] angles = driver.initAngles();
		String initStr = clientId + "(init";
		for (int i = 0; i < angles.length; i++) {
			initStr = initStr + " " + angles[i];
		}
		initStr = initStr + ")";


		/* Build GA population */
		Population myPop = new Population(50, true);
		int generationCount = 0;

		/* Initialize some variables */
		long curEpisode = 0;
		boolean shutdownOccurred = false;
		int stepLimit = 14300; // acceptable steps for this track
		int generationLimit = 5;
		Stats mainStats = new Stats(generationLimit, myPop.size());
		do {
			while (generationCount < generationLimit){
				generationCount++;
				System.out.println("Generation: " + generationCount);
				for(int i = 0; i<myPop.size(); i++){
					driver.setParameters(myPop.getIndividual(i).getAllGenes());

					/*
					 * Client identification
					 */
					do {
						mySocket.send(initStr);
						inMsg = mySocket.receive(UDP_TIMEOUT);
					} while (inMsg == null || inMsg.indexOf("***identified***") < 0);

					/*
					 * Start to drive
					 */
					double fitness = 0;
					boolean fitnessSet = false;
					boolean isLastGuy = ((generationCount == generationLimit) && (i+1 == myPop.size()));
					long currStep = 0;
					while (true) {
						/*
						 * Receives from TORCS the game state
						 */
						inMsg = mySocket.receive(UDP_TIMEOUT);

						if (inMsg != null) {

							/*
							 * Check if race is ended (shutdown)
							 */
							if (inMsg.indexOf("***shutdown***") >= 0) {
								shutdownOccurred = true;
								System.out.println("Server shutdown!");
								break;
							}

							/*
							 * Check if race is restarted
							 */
							if (inMsg.indexOf("***restart***") >= 0) {
								driver.reset();
								if (verbose)
									System.out.println("Server restarting!");
								break;
							}

							Action action = new Action();
							if ((currStep < maxSteps || maxSteps == 0) && !fitnessSet)	// check this
								action = driver.control(new MessageBasedSensorModel(inMsg));
							else
							{
								if(!isLastGuy) {
									action.restartRace = true;
									System.out.println(fitness);
									System.out.println(currStep);
								}
								else // run until game closes
									action = driver.control(new MessageBasedSensorModel(inMsg));

							}

							if(currStep > stepLimit){
								System.out.println("Possible 0 reset");
								i = resetIndividual(myPop, i, currStep);
								action.restartRace = true;
							}

							currStep++;
							mySocket.send(action.toString());
							fitness = driver.getLastLapTime();
							if(fitness != 0 && !fitnessSet) {
								myPop.getIndividual(i).setNewFitness(fitness);
								mainStats.addData(i, generationCount-1, fitness);
								fitnessSet = true;
							}

						} else
							System.out.println("Server did not respond within the timeout");
					}
				}
				if(generationCount != generationLimit)
					myPop = Algorithm.evolvePopulation(myPop);
			}
		} while (++curEpisode < maxEpisodes && !shutdownOccurred);

		/*
		 * Shutdown the controller
		 */
		driver.shutdown();
		mySocket.close();
		System.out.println("shutdownOccurred is "+shutdownOccurred);
		System.out.println("Client shutdown.");
		System.out.println("Bye, bye!");
		System.out.println(myPop.getFittest().toString());
		System.out.println(myPop.getFittest().getIndividualFitness());
		mainStats.processData();
		System.out.println(mainStats.toString());

	}

	private static int resetIndividual(Population myPop, int i, long currStep) {
		System.out.println(myPop.getIndividual(i).toString());
		System.out.println(currStep);
		myPop.getIndividual(i).generateIndividual();
		i--;
		System.out.println("Reset individual values");
		return i;
	}

	private static void parseParameters(String[] args) {
		/*
		 * Set default values for the options
		 */
		port = 3001;
		host = "localhost";
		clientId = "championship2010";
		verbose = false;
		maxEpisodes = 1;
		maxSteps = 0;
		stage = Stage.UNKNOWN;
		trackName = "unknown";

		for (int i = 1; i < args.length; i++) {
			StringTokenizer st = new StringTokenizer(args[i], ":");
			String entity = st.nextToken();
			String value = st.nextToken();
			if (entity.equals("port")) {
				port = Integer.parseInt(value);
			}
			if (entity.equals("host")) {
				host = value;
			}
			if (entity.equals("id")) {
				clientId = value;
			}
			if (entity.equals("verbose")) {
				if (value.equals("on"))
					verbose = true;
				else if (value.equals(false))
					verbose = false;
				else {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}
			}
			if (entity.equals("id")) {
				clientId = value;
			}
			if (entity.equals("stage")) {
				stage = Stage.fromInt(Integer.parseInt(value));
			}
			if (entity.equals("trackName")) {
				trackName = value;
			}
			if (entity.equals("maxEpisodes")) {
				maxEpisodes = Integer.parseInt(value);
				if (maxEpisodes <= 0) {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}
			}
			if (entity.equals("maxSteps")) {
				maxSteps = Integer.parseInt(value);
				if (maxSteps < 0) {
					System.out.println(entity + ":" + value
							+ " is not a valid option");
					System.exit(0);
				}
			}
		}
	}

	private static Controller load(String name) {
		Controller controller=null;
		try {
			controller = (Controller) (Object) Class.forName(name)
					.newInstance();
		} catch (ClassNotFoundException e) {
			System.out.println(name	+ " is not a class name");
			System.exit(0);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return controller;
	}
}
