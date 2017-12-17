import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FirewallManager {
	
	/* create log-analyzer chain
	 * iptables -N log-analyzer
	 * 
	 * add chain to input
	 * iptables -t filter -A INPUT -j log-analyzer
	 * 
	 * ban ip
	 * iptables -A log-analyzer -s 65.55.44.100 -j DROP
	 * (DROP -> no answer, REJECT -> send "unreachable")
	 * 
	 * remove ban
	 * iptables -D log-analyzer -s 79.16.18.136 -j DROP
	 * 
	 * */
	
	private static ArrayList<String> bannedIps = new ArrayList<String>();
	
	public static void banIp(String ip) {
		createChain();
		if (!isChainAddedToInput()) executeCommand("iptables -t filter -A INPUT -j log-analyzer", true);
		if (!isAlredyBanned(ip)) executeCommand("iptables -A log-analyzer -s "+ip+" -j DROP", true);
		else print("The ip \""+ip+"\" is alredy banned!");
	}
	
	public static boolean isChainAddedToInput() {
		String[] lines = executeCommand("iptables -L INPUT -n").split("\n");
		for (String line: lines) {
			String[] words = line.split(" +");
			if (words[0].equals("log-analyzer")) return true;
		}
		return false;
	}
	
	public static void createChain() {
		executeCommand("iptables -N log-analyzer");
	}
	
	public static void removeBan(int index) {
		buildBannedIpList();
		if (bannedIps.isEmpty()) {
			print("There are no banned ips!");
		} else if (index<0||index>=bannedIps.size()) {
			print("Invalid index!");
		} else {
			executeCommand("iptables -D log-analyzer -s "+bannedIps.get(index)+" -j DROP");
		}
		buildBannedIpList();
	}
	
	public static boolean isAlredyBanned(String ip) { 
		buildBannedIpList();
		return bannedIps.contains(ip);
	}
	
	public static ArrayList<String> getBannedIpList() {
		buildBannedIpList();
		return bannedIps;
	}
	
	public static void buildBannedIpList() {
		bannedIps = new ArrayList<String>();
		String[] lines = executeCommand("iptables -L log-analyzer -n").split("\n");
		boolean targetFound = false;
		for (String line: lines) {
			if (line.split(" +")[0].equals("target")) targetFound = true;
			else if (targetFound) {
				bannedIps.add(line.split(" +")[3]);
			}
		}
	}
	
	private static String executeCommand(String com) {
		return executeCommand(com,false);
	}
	
	private static String executeCommand(String com, boolean print) {
		if (print) print("Executing command: "+com);
		try {
			Process p = Runtime.getRuntime().exec(com);
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String s = null;
			String res = "";
			while ((s = stdInput.readLine()) != null) {
	            res = res+s+"\n";
	        }
			return res;
		} catch (IOException e) {System.err.println("Error occurred");}
		return "";
	}
	
	public static void printIpTables() {
		executeCommand("iptables -L -n",true);
	}
	
	private static void print(String s) {
		System.out.println(s);
	}

}
