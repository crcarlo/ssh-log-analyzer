
public class ConnectionInfo {
	
	public String ip;
	public String domainName;
	public String countryCode;
	public boolean regionFromTLD = false;
	public int failedAttempts;
	public String lastAttemptDate;
	public int succesfulConnections;
	public String lastSuccesfulConnection;
	
	public ConnectionInfo(String ip) {
		this.ip=ip;
	}
	
	public String save() {
		return this.ip+" "+this.domainName+" "+this.countryCode+" "+this.failedAttempts+" "+this.lastAttemptDate;
	}
	
	public void load(String s) {
		String[] arr = s.split(" ");
		this.ip = arr[0];
		this.domainName = arr[1];
		this.countryCode = arr[2];
		this.failedAttempts = Integer.parseInt(arr[3]);
		this.lastAttemptDate = arr[4];
	}
	
	@Override
	public String toString() {
		boolean failedAttempt = (this.failedAttempts>0)?true:false;
		boolean successfulConnections = (this.succesfulConnections>0)?true:false;
		return "IP: "+this.ip+" DN: "+this.domainName+" COUNTRY: "+(countryCode==null||countryCode.equals("null")?"-":countryCode)
				+ (failedAttempt?" FAILED ATTEMPTS: "+this.failedAttempts+" LAST ON "+this.lastAttemptDate:"")
				+ (successfulConnections?" SUCCESFUL CONNECTIONS: "+this.succesfulConnections+" LAST ON "+this.lastSuccesfulConnection:"")
				;
	}

}
