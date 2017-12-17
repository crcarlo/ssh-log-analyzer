package ipInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class RegionDetect {
	
	public static final String country_codes_file_path = "country_codes.csv";
	public static final String csv_zone_directory_path = "csv_zones/";
	
	public static ArrayList<Zone> zones = new ArrayList<Zone>();
	public static ArrayList<CountryCode> countryCodes = new ArrayList<CountryCode>();
	
	public static String getIpRegion(String ip) throws InvalidIpException {
		if (countryCodes.isEmpty())
			try {
				RegionDetect.initialize();
			} catch (MissingFileComponentException e) {
				e.printStackTrace();
			}
		for (Zone zone:zones) {
			if (zone.isIpInZone(ip)) return getCountryName(zone.getCountryCode());
		}
		return null;
	}

	public static String getRegionFromTLD(String dn) {
		String[] parts = dn.split("\\.");
		String tld = parts[parts.length-1];
		for (CountryCode code: countryCodes) {
			if (code.getCountryCode().equals(tld)) return code.getCountryCode();
		}
		return null;
	}

	public static void initialize() throws MissingFileComponentException {
		RegionDetect.loadCountryCodes();
		RegionDetect.loadZones();
	}
	
	private static void loadZones() {
		zones = new ArrayList<Zone>();
		for (CountryCode code:countryCodes) {
			try {
				String filePath = csv_zone_directory_path+code.getCountryCode()+".csv";
				String countryCode = code.getCountryCode();
				ArrayList<IpRange> ranges = new ArrayList<IpRange>();
				for (String line:readFile(filePath)) {
					try {
						String[] split = line.split(",");
						ranges.add(new IpRange(
								new IP(split[0]),
								new IP(split[1]),
								Integer.parseInt(split[2]),
								split[3]));
					} catch (ArrayIndexOutOfBoundsException | InvalidIpException e) {}
				}
				zones.add(new Zone(countryCode,ranges));
			} catch (IOException e) {}
		}		
	}

	private static void loadCountryCodes() throws MissingFileComponentException {
		try {
			for (String line:readFile(country_codes_file_path)) {
				String[] split = line.split(",");
				countryCodes.add(new CountryCode(split[0].toLowerCase(), split[1]));
			}
		} catch (IOException e) {
			throw new MissingFileComponentException(country_codes_file_path);
		}
	}
	
	private static String getCountryName(String countryCode) {
		for (CountryCode code:countryCodes) {
			if (countryCode.equals(code.getCountryCode())) return code.getCountryName();
		}
		return null;
	}
	
	private static String[] readFile(String filePath) throws IOException {
		BufferedReader br = null;
		BufferedReader br1 = null;
		String[] result=null;
		String sCurrentLine;
		br = new BufferedReader(new FileReader(filePath));
		br1 = new BufferedReader(new FileReader(filePath));
		int numberOfLines=0;
		while ((sCurrentLine = br1.readLine()) != null) numberOfLines++;
		result = new String[numberOfLines];
		int i=0;
		while ((sCurrentLine = br.readLine()) != null) {
			result[i]=sCurrentLine;
			i++;
		}
		if (br != null)br.close();
		if (br1 != null)br1.close();
		return result;
	}
}
