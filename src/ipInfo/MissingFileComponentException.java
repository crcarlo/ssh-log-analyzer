package ipInfo;

public class MissingFileComponentException extends Exception {
	private static final long serialVersionUID = 1L;
	
	private String filePath;
	
	public MissingFileComponentException(String filePath) {
		super();
		this.filePath=filePath;
	}
	
	@Override
	public void printStackTrace() {
		System.err.println("Missing file: "+filePath);
		super.printStackTrace();
	}

}