/**
 *
 */
package champ2010client;

import java.util.StringTokenizer;

import champ2010client.Controller.Stage;

/**
 * @author Daniele Loiacono
 *
 */
public class ClientGAResult {

	private static int UDP_TIMEOUT = 2000;
	private static int port;
	private static String host;
	private static String clientId;
	private static boolean verbose;
	private static int maxEpisodes;
	private static int maxSteps;
	private static Stage stage;
	private static String trackName;

	/**
	 * @param resultSet
	 *            is used to define the driver behaviour.
	 *            <port:N> is used to specify the port for the connection (default is 3001)
	 *            <host:ADDRESS> is used to specify the address of the host where the server is running (default is localhost)
	 *            <id:ClientID> is used to specify the ID of the client sent to the server (default is championship2009)
	 *            <verbose:on> is used to set verbose mode on (default is off)
	 *            <maxEpisodes:N> is used to set the number of episodes (default is 1)
	 *            <maxSteps:N> is used to set the max number of steps for each episode (0 is default value, that means unlimited number of steps)
	 *            <stage:N> is used to set the current stage: 0 is WARMUP, 1 is QUALIFYING, 2 is RACE, others value means UNKNOWN (default is UNKNOWN)
	 *            <trackName:name> is used to set the name of current track
	 */
	public static void main(double[] resultSet) {
		parseParameters();
		SocketHandler mySocket = new SocketHandler(host, port, verbose);
		String inMsg;

		Controller driver = load("champ2010client.GA.GenAlgDriver");
		driver.setParameters(resultSet);
		driver.setStage(stage);
		driver.setTrackName(trackName);

		/* Build init string */
		float[] angles = driver.initAngles();
		String initStr = clientId + "(init";
		for (int i = 0; i < angles.length; i++) {
			initStr = initStr + " " + angles[i];
		}
		initStr = initStr + ")";

		long curEpisode = 0;
		boolean shutdownOccurred = false;
		do {

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
					if (currStep < maxSteps || maxSteps == 0)
						action = driver.control(new MessageBasedSensorModel(
								inMsg));
					else
						action.restartRace = true;

					currStep++;
					mySocket.send(action.toString());
				} else
					System.out.println("Server did not respond within the timeout");
			}

		} while (++curEpisode < maxEpisodes && !shutdownOccurred);

		/*
		 * Shutdown the controller
		 */
		driver.shutdown();
		mySocket.close();
		System.out.println("Client shutdown.");
		System.out.println("Bye, bye!");

	}

	private static void parseParameters() {
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
