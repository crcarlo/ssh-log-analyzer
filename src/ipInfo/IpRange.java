package ipInfo;

public class IpRange {
	
	private IP startIp;
	private IP endIp;
	private int containedIpsNumber;
	private String prividerName;
	
	public IpRange(IP startIp, IP endIp, int containedIpsNumber, String prividerName) {
		this.startIp = startIp;
		this.endIp = endIp;
		this.containedIpsNumber = containedIpsNumber;
		this.prividerName = prividerName;
	}
	
	public boolean isIpInRange(String ip) throws InvalidIpException {
		return IP.isIpInRange(new IP(ip), this.startIp, this.endIp);
	}
	
	public IP getStartIp() {
		return startIp;
	}
	
	public IP getEndIp() {
		return endIp;
	}
	
	public int getContainedIpsNumber() {
		return containedIpsNumber;
	}
	
	public String getPrividerName() {
		return prividerName;
	}

	@Override
	public String toString() {
		return "IpRange [startIp=" + startIp + ", endIp=" + endIp + ", containedIpsNumber=" + containedIpsNumber
				+ ", prividerName=" + prividerName + "]";
	}
	
	

}
