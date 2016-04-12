package pt.upa.broker.ws;

public class ClientHandlerProblems_Exception extends Exception{

	private static final long serialVersionUID = 1L;
	private String msg = "Check if ";
	
	public ClientHandlerProblems_Exception() {
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
