package pt.upa.broker.ws;

public class FaultManager extends Thread{

	private boolean alive;
	private String wsURL;
	
	public FaultManager(String name){
		setWsURL(name);
		setAlive(true);
	}

	public String getWsURL() {
		return wsURL;
	}

	public void setWsURL(String wsURL) {
		this.wsURL = wsURL;
	}
	
	public void setAlive(boolean bool){
		alive = bool;
	}
	
	public void run() {
		while(alive){
			
		}
	}

	 
}