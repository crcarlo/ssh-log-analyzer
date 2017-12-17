import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalyzedLog {
	
	public static final String backup_file_path = "/log/save.log";
	
	public static ArrayList<ConnectionInfo> infos = new ArrayList<ConnectionInfo>();
	public static String firstDate = "";
	public static String lastDate = "";
	public static int totalFailedAttempts = 0;
	public static int totalSuccessfulAttempts = 0;
	public static ArrayList<String> countryCodes = new ArrayList<String>();
	public static int[] regCountF;
	public static int[] regNodesF;
	public static int[] regCountS;
	public static int[] regNodesS;
	
	private static boolean needToDisplayLegenda = false;
	
	public static void analyze(String[] logContent) {
		System.out.println("analyzing... (may take a while dependig on the log file size and cache)");
		ArrayList<String> failedLines = new ArrayList<String>();
		for (String line: logContent) {
			if (line.contains("Failed password")) failedLines.add(line);
			if (line.contains("Accepted publickey")) failedLines.add(line);
		}
		String IPADDRESS_PATTERN = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
		Pattern pattern = Pattern.compile(IPADDRESS_PATTERN);
		ArrayList<String> ips = new ArrayList<String>();
		for (String line: failedLines) {
			Matcher matcher = pattern.matcher(line);
			if (matcher.find()) {
			    ips.add(matcher.group());
			} else {}
		}
		ArrayList<String> ipsNoDuplicates = new ArrayList<String>();
		for (String ip:ips) {
			if (!ipsNoDuplicates.contains(ip)) ipsNoDuplicates.add(ip);
		}
		infos = new ArrayList<ConnectionInfo>();
		for (String ip:ipsNoDuplicates) {
			infos.add(new ConnectionInfo(ip));
		}
		for (ConnectionInfo info : infos) {
			int countFailed=0;
			int countSuccess=0;
			String lastFailDate = "none";
			String lastSuccessDate = "none";
			for (String line:failedLines) {
				if (line.contains(info.ip)&&line.contains("Failed password")) {
					countFailed++;
					lastFailDate = String.join(" ", line.split(" +")[0], line.split(" +")[1],line.split(" +")[2]);
				}
				if (line.contains(info.ip)&&line.contains("Accepted publickey")) {
					countSuccess++;
					lastSuccessDate = String.join(" ", line.split(" +")[0], line.split(" +")[1],line.split(" +")[2]);
				}
			}
			info.succesfulConnections=countSuccess;
			info.lastSuccesfulConnection=lastSuccessDate;
			info.failedAttempts=countFailed;
			info.lastAttemptDate=lastFailDate;
			info.domainName = IpInfoManager.detectDomainName(info.ip);
			if (info.domainName.equals(info.ip)) info.domainName = "-";
			info.countryCode = IpInfoManager.obtainRegion(info.ip,info.domainName);
			System.out.println(info);
		}
		//if (needToDisplayLegenda) System.out.println("* obtained from TLD of DN");
		//get first and last date
		firstDate = String.join(" ", 
				logContent[0].split(" +")[0], 
				logContent[0].split(" +")[1],
				logContent[0].split(" +")[2]);
		lastDate = String.join(" ", 
				logContent[logContent.length-1].split(" +")[0], 
				logContent[logContent.length-1].split(" +")[1],
				logContent[logContent.length-1].split(" +")[2]);
		System.out.println("\nLog file period: "+firstDate+" to "+lastDate);
		printFinalReport();
	}
	
	public static void printFinalReport() {
		//total failed logs
		totalFailedAttempts = 0;
		totalSuccessfulAttempts = 0;
		for (ConnectionInfo info: infos) {
			totalFailedAttempts += info.failedAttempts;
			totalSuccessfulAttempts += info.succesfulConnections;
		}
		System.out.println("Total failed attemtps: "+totalFailedAttempts);
		//count per region
		System.out.println("Attempts per region: ");
		for (ConnectionInfo info: infos) {
			if (!countryCodes.contains(info.countryCode)) countryCodes.add(info.countryCode);
		}
		regCountF = new int[countryCodes.size()];
		regNodesF = new int[countryCodes.size()];
		regCountS = new int[countryCodes.size()];
		regNodesS = new int[countryCodes.size()];
		int countF = 0;
		int countS = 0;
		for (int i=0; i<countryCodes.size(); i++) {
			countF = 0;
			countS = 0;
			for (ConnectionInfo info: infos) {
				if (countryCodes.get(i).equals(info.countryCode)) {
					countF+=info.failedAttempts;
					countS+=info.succesfulConnections;
					if (info.failedAttempts!=0) regNodesF[i]++;
					if (info.succesfulConnections!=0) regNodesS[i]++;
				}
			}
			regCountF[i]=countF;
			regCountS[i]=countS;
			if (countF!=0&&regNodesF[i]!=0) System.out.println("\t"+countryCodes.get(i)+": "+countF+" connections from "+regNodesF[i]+" nodes");
		}
		System.out.println("Total successful connections: "+totalSuccessfulAttempts);
		System.out.println("Connections per region: ");
		for (int i=0; i<countryCodes.size(); i++) {
			if (regCountS[i]!=0&&regNodesS[i]!=0) System.out.println("\t"+countryCodes.get(i)+": "+regCountS[i]+" connections from "+regNodesS[i]+" nodes");
		}
		
	}
	
	public static void printRegions(boolean ind) {
		for (int i=0; i<countryCodes.size(); i++) {
			System.out.println((ind ? "["+i+"] " : "") + countryCodes.get(i));
		}
	}
	
	public static void printConnectionInfos(boolean ind, boolean banned) {
		for (int i=0; i<infos.size(); i++) {
			if (!FirewallManager.isAlredyBanned(infos.get(i).ip)) System.out.println((ind ? "["+i+"] " : "")+infos.get(i));
		}
		
	}
	
	public static void printConnectionInfos(boolean ind, String type) {
		ArrayList<ConnectionInfo> sel = new ArrayList<ConnectionInfo>();
		for (ConnectionInfo info:infos) {
			if (type.equals("failed")&&info.failedAttempts!=0) sel.add(info);
			if (type.equals("success")&&info.succesfulConnections!=0) sel.add(info);
		}
		for (int i=0; i<sel.size(); i++) {
			System.out.println((ind ? "["+i+"] " : "")+sel.get(i));
		}
	}
	
	public static void print() {
		printConnectionInfos(false,true);
		System.out.println();
		System.out.println("Log file period: "+firstDate+" to "+lastDate);
		System.out.println("Total failed attempts: "+totalFailedAttempts);
		System.out.println("Attempts per region:");
		for (int i=0; i<countryCodes.size(); i++) {
			System.out.println("\t"+countryCodes.get(i)+": "+regCountF[i]+"_connections from: "+regNodesF[i]+"_nodes");
		}
	}

}
