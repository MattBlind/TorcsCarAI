package evaluator;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 * @author Luigi
 *
 */
public class ServerCommunication{

	static final private int maxlen=128;
	static final private int maxtry=600;
	static final private int clientport=4001;
	private DatagramSocket socket;
	private FitnessFunction fitness;
	private InetAddress address;
	private long remainingtime;
	private int numparams;
	private int port;
	
	public long getRemainingTime(){
		return remainingtime;
	}
	
	public int getParamNumber(){
		return numparams;
	}

	public ServerCommunication(String ip, int port){
		//Default fitness function
		this.fitness = new DefaultFitness();
		
		try {
			this.socket = new DatagramSocket(clientport);
			this.address = InetAddress.getByName(ip);
			this.port = port;
			socket.setSoTimeout(1000);
			
			boolean identified =false;
			DatagramPacket packet;
			for (int i=0; i< maxtry && !identified; i++){
				byte[] buf = new String("info?\0").getBytes();
				packet = new DatagramPacket(buf, buf.length, address, port);
				socket.send(packet);
				
				buf = new byte[maxlen];
				packet = new DatagramPacket(buf, maxlen);
				try {
					socket.receive(packet);
					String[] received = (new String(packet.getData(), 0,packet.getLength())).split(" ");

					if (received.length > 2)
						if (received[0].equals("info")){
							numparams = Integer.parseInt(received[1]);
							remainingtime = Integer.parseInt(received[2]);
							identified = true;
							System.out.println("Identified! params: " + numparams + " simutime: " + remainingtime);
						}

				} catch (SocketTimeoutException e1) {
					//System.out.println("waiting...");
				}
				
			}
			
			socket.setSoTimeout(0);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection error!");
		}		
	}
	
	public void setFitnessFunction(FitnessFunction fitness){
		this.fitness = fitness;
	}
	
	public void saveBest(double[] values) throws Exception {
		launchSimulation(values,0);
	}
	
	public Object launchSimulation(double[] values, int time) throws Exception {
		double fit1=0, fit2=0, fit3=0, fit4=0;
		
		if (values.length < numparams)
			throw new Exception();
		
		remainingtime = remainingtime - time;
		
		DatagramPacket packet;
		
		//Send evalutation string
		String params = "eval " + time;
		for (int i=0; i<values.length; i++)
			params = params + " " + values[i];
		byte[] buf = new String(params + "\0").getBytes();
		packet = new DatagramPacket(buf, buf.length, address, port);
		socket.send(packet);
		
		//Receive fitness
		buf = new byte[maxlen];
		packet = new DatagramPacket(buf, maxlen);
		socket.receive(packet);
		String[] received = (new String(packet.getData(), 0,packet.getLength())).split(" ");

		if (received.length == 5 && received[0].equals("result")){
			fit1 = Double.parseDouble(received[1]);
			fit2 = Double.parseDouble(received[2]);
			fit3 = Double.parseDouble(received[3]);
			fit4 = Double.parseDouble(received[4]);
			return fitness.getFitness(fit1, fit2, fit3, fit4);
		}
		
		if (received.length == 1 && received[0].equals("time-over"))
			throw new TimeOverException();
			
		return null;
	}
	
	public void close(){
		socket.close();
	}

}

