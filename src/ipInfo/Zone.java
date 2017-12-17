package ipInfo;

import java.util.ArrayList;

public class Zone {
	
	private String countryCode;
	private ArrayList<IpRange> ranges;
	
	public Zone(String countryCode, ArrayList<IpRange> ranges) {
		this.countryCode=countryCode;
		this.ranges=ranges;
	}
	
	public String getCountryCode() {
		return this.countryCode;
	}
	
	public boolean isIpInZone(String ip) throws InvalidIpException {
		for (IpRange range:ranges) if (range.isIpInRange(ip)) return true;
		return false;
	}

}
