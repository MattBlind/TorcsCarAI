package evaluator;

public class TimeOverException extends Exception{
	  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TimeOverException() {
		    super("Simulation Time is Over!");
		  }
}
