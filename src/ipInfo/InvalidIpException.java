package ipInfo;

public class InvalidIpException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String ip;
	
	public InvalidIpException(String ip) {
		super();
		this.ip=ip;
	}
	
	@Override
	public void printStackTrace() {
		System.err.println("Invalid IP: "+ip);
		super.printStackTrace();
	}

}
