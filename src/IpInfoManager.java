import java.net.InetAddress;
import java.net.UnknownHostException;

import ipInfo.InvalidIpException;
import ipInfo.RegionDetect;
import stringFile.StringFileLoader;

public class IpInfoManager {
	
	public static String dn_cache_path = "cache/dncache.csv";
	public static String ip_country_cache_path = "cache/ipcountrycache.csv";

	public static String detectDomainName(String ip) {
		String dncache = getDomainNameFromCache(ip);
		if (dncache!=null) return dncache;
		try {
			String[] s = ip.split("\\.");
			byte[] b = new byte[4];
			for (int i=0; i<4; i++) {
				b[i] = (byte)Integer.parseInt(s[i]);
			}
			InetAddress ia = InetAddress.getByAddress(new byte[] {b[0],b[1],b[2],b[3]});
			String dn = ia.getCanonicalHostName();
			saveDomainNameInCache(ip,dn);
			return dn;
		} catch (UnknownHostException e) {
			return "error_occurred";
		}
	}
	
	private static void saveIpCountryInCache(String ip, String countryCode) {
		StringFileLoader loader = new StringFileLoader(ip_country_cache_path);
		loader.addNewLine(ip+","+countryCode);
		loader.save();
	}
	
	private static String getIpCountryFromCache(String ip) {
		try {
			StringFileLoader loader = new StringFileLoader(ip_country_cache_path);
			for (String line:loader.fileContent) {
				if (line.split(",")[0].equals(ip)) return line.split(",")[1];
			}
		} catch (NullPointerException e) {
			
		}
		return null;
	}
	
	private static void saveDomainNameInCache(String ip, String dn) {
		try {
			StringFileLoader loader = new StringFileLoader(dn_cache_path);
			loader.addNewLine(ip+","+dn);
			loader.save();
		} catch (NullPointerException e) {
			StringFileLoader loader = new StringFileLoader(dn_cache_path);
			loader.addNewLine(ip+","+dn);
			loader.save();
		}
	}
	
	private static String getDomainNameFromCache(String ip) {
		try {
			StringFileLoader loader = new StringFileLoader(dn_cache_path);
			for (String line:loader.fileContent) {
				if (line.split(",")[0].equals(ip)) return line.split(",")[1];
			}
		} catch (NullPointerException e) {}
		return null;
	}

	public static String obtainRegion(String ip, String domainName) {
		try {
			String cacheRegion = getIpCountryFromCache(ip);
			if (cacheRegion!=null) return cacheRegion;
			String region = RegionDetect.getIpRegion(ip);
			saveIpCountryInCache(ip,region);
			return region;
		} catch (InvalidIpException e) {
			e.printStackTrace();
			return null;
		}
	}

}
