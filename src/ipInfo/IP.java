package ipInfo;
import java.util.regex.Pattern;

public class IP {
	
	public byte[] ip = new byte[4];
	
	public IP(String ip) throws InvalidIpException {
		if (!IP.isValidIP(ip)) throw new InvalidIpException(ip);
		String[] ipBytes = ip.split("\\.");
		for (int i=0; i<4; i++) {
			this.ip[i] = (byte)Integer.parseInt(ipBytes[i]);
		}
	}
	
	/**
	 * Checks if the current object IP is lower than the argument object "a".
	 * If it's lower returns true, otherwise returns false.
	 * */
	public boolean isLowerThan(IP a) {
		for (int i=0; i<4; i++) {
			if (this.ip[i]<a.ip[i]) return true;
			if (this.ip[i]>a.ip[i]) return false;
		}
		return false;
	}
	
	public boolean equals(IP newip) {
		for (int i=0; i<4; i++) {
			if (newip.ip[i]!=this.ip[i]) return false;
		}
		return true;
	}
	
	public String toString() {
		String s = "";
		for (int i=0; i<4; i++) {
			s+=ip[i];
			if (i!=3) s+=".";
		}
		return s;
	}
	
	public static boolean isIpInRange(IP ip, IP startIp, IP endIp) {
		if (IP.isHigherOrEqual(ip,startIp)&&IP.isLowerOrEqual(ip, endIp)) return true;
		else return false;
	}
	
	private static boolean isLowerOrEqual(IP ip, IP compIp) {
		if (ip.equals(compIp)) return true;
		if (ip.isLowerThan(compIp)) return true;
		else return false;
	}

	private static boolean isHigherOrEqual(IP ip, IP compIp) {
		if (ip.equals(compIp)) return true;
		if (compIp.isLowerThan(ip)) return true;
		else return false;
	}

	public static boolean isValidIP(String ip) {
		String regex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
		Pattern pattern = Pattern.compile(regex);
	    if (!pattern.matcher(ip).matches()) return false;
	    else return true;
	}
	
}