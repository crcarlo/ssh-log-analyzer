import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import stringFile.StringFileLoader;

public class ConnectionLogAnalyzer {
	
	//public static String log_file_path = "/var/log/auth.log";
	public static String log_file_path = "auth.log";
	
	public static void main(String[] args) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = "";
		while (!(s.equals("quit")||s.equals("q"))) {
			print();
			print("=======================");
			print("log analyzer v1.1\nAuthor: Carlo Cervellin");
			print("=======================");
			print("type:");
			print("\t[q] or [quit] for quitting");
			print("\t===========ANALYZE===========");
			print("\t[a] or [analyze] for analyzing log file");
			print("\t[l] or [list] for wiewing last analysis report");
			print("\t[lf] or [listfailed] for wiewing failed attempts from last analysis report");
			print("\t[ls] or [listsuccess] for wiewing successful attempts from last analysis report");
			print("\t===========BAN===============");
			print("\tBanned ips will be dropped from log-analyzer chain in ip-tables");
			print("\t[b] or [ban] for banning a single ip");
			print("\t[br] or [banregion] for banning ips from an entire region (only the ips from that region which failed a connection)");
			print("\t[brm] or [banremove] for removing ban of an ip");
			print("\t[bv] or [banview] for displaying banned ips (shows raw iptables)");
			print("\t===========INFO==============");
			print("\t[log] for viewing auth.log file");
			print("\t[logp] for viewing log file path");
			print();
			printNwait();
			try {
				s = br.readLine();
			} catch (IOException e) {}
			if (s.equals("a")||s.equals("analyze")) AnalyzedLog.analyze(getLogFileContent());
			if (s.equals("l")||s.equals("list")) {
				if (AnalyzedLog.infos.isEmpty()) print("No previous analysis, type [a] to make one now");
				else AnalyzedLog.print();
			}
			if (s.equals("lf")||s.equals("listfailed")) {
				if (AnalyzedLog.infos.isEmpty()) print("No previous analysis, type [a] to make one now");
				else AnalyzedLog.printConnectionInfos(false, "failed");
			}
			if (s.equals("ls")||s.equals("listsuccess")) {
				if (AnalyzedLog.infos.isEmpty()) print("No previous analysis, type [a] to make one now");
				else AnalyzedLog.printConnectionInfos(false, "success");
			}
			if (s.equals("b")||s.equals("ban")) banIp();
			if (s.equals("br")||s.equals("banregion")) banRegion();
			if (s.equals("brm")||s.equals("banremove")) removeBan();
			if (s.equals("bv")||s.equals("banview")) printBannedIpList(false);
			if (s.equals("log")) print(getLogFileContent());
			if (s.equals("logp")) print(log_file_path);
		}
		print("quitting...");
	}
	
	private static void removeBan() {
		printBannedIpList(true);
		print("Which ip do you want to remove from ban list? (type anything else to go back)");
		int num;
		try {
			num = getInputInt();
		} catch (NullPointerException e) {
			return;
		}
		FirewallManager.removeBan(num);
	}
	
	public static void banIp() {
		if (AnalyzedLog.infos.isEmpty()) {
			print("Do analyze first!");
			return;
		}
		AnalyzedLog.printConnectionInfos(true,false);
		print("Which IP do you want to ban? (type just the number, type anything else to go back)");
		printNwait();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = "";
		try {
			s = br.readLine();
		} catch (IOException e) {}
		try {
			int num = Integer.parseInt(s);
			FirewallManager.banIp(AnalyzedLog.infos.get(num).ip);
		} catch (NumberFormatException e) {
		}
		
	}
	
	public static void banRegion() {
		if (AnalyzedLog.infos.isEmpty()) {
			print("Do analyze first!");
			return;
		}
		AnalyzedLog.printConnectionInfos(false,false);
		AnalyzedLog.printRegions(true);
		print("Which region do you want to ban? (type just the number, type anything else to go back)");
		int num;
		try {
			num = getInputInt();
		} catch (NullPointerException e) {
			return;
		}
		String countryCode = AnalyzedLog.countryCodes.get(num);
		for (ConnectionInfo info:AnalyzedLog.infos) {
			if (info.countryCode.equals(countryCode)) FirewallManager.banIp(info.ip);
		}
		
	}
	
	public static Integer getInputInt() {
		printNwait();
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String s = "";
		try {
			s = br.readLine();
		} catch (IOException e) {}
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	private static void printBannedIpList(boolean displayNumber) {
		ArrayList<String> bannedIps = FirewallManager.getBannedIpList();
		for (int i=0; i<bannedIps.size(); i++) {
			print(displayNumber?"["+i+"] ":""+bannedIps.get(i));
		}
	}
	
	public static String[] getLogFileContent() {
		return (new StringFileLoader(log_file_path)).fileContent;
	}
	
	private static void print(String s) {
		System.out.println(s);
	}
	
	private static void print() {
		System.out.println();
	}
	
	private static void printNwait() {
		System.out.print(">>>");
	}
	
	private static void print(String[] arr) {
		for (String s : arr) {
			System.out.println(s);
		}
	}

}
