package de.floaz.systeminfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Enumeration;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;


public class GetSystemInfo {

	static final boolean FORCE_ROOT_USER = false;
	static final boolean ONLY_IPV4_ADDRESSES = true;


    /**
	 * Main application
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		GetSystemInfo a = new GetSystemInfo();
		a.launch();
    }


    /**
     * Launch the application.
     */
    public void launch() {
		// Initialize colored output.
		AnsiConsole.systemInstall();

		if(!checkUser()) {
			return;
		}

		displayOs();
		displayHostname();
		displayHardware();
		displayNetworkInterfaces();
    }


    protected boolean checkUser() {
		String username = System.getProperty("user.name");
		if(FORCE_ROOT_USER && !username.equals("root")) {
			AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.RED)
					.a("You must run this program as root user!")
					.reset());

			return false;
		}

		return true;
     }


    protected void displayOs() {
		AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).newline().a("Operating System:").reset());

		String osName = System.getProperty("os.name");
		if(osName.equals("Linux")) {
			String distriString = cmdExec("lsb_release -d");
			osName += distriString.substring(distriString.indexOf(":") +1);
			osName = osName.replaceAll("\n", "");
		}
		AnsiConsole.out.println(osName);
     }


    protected void displayHostname() {
		AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).newline().a("Hostname:").reset());

		String localhostname;
		try {
			localhostname = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException ex) {
			AnsiConsole.out.println("Not available!");
			return;
		}
		AnsiConsole.out.println(localhostname);
     }


    protected void displayHardware() {
		AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).newline().a("Hardware:").reset());
		int cores = Runtime.getRuntime().availableProcessors();
		long memorySize = ((com.sun.management.OperatingSystemMXBean) ManagementFactory
													.getOperatingSystemMXBean()).getTotalPhysicalMemorySize();

		AnsiConsole.out.println("CPU Kerne = " + Integer.toString(cores));
		AnsiConsole.out.println("RAM in MB = " + Long.toString(memorySize / (1024 * 1024)));
     }


    protected void displayNetworkInterfaces() {
		AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.CYAN)
									.newline().newline()
									.a("Network interfaces:")
									.reset());
		try {
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
			for (NetworkInterface netint : Collections.list(nets)) {
				displayNetworkInterface(netint);
			}
		}
		catch(SocketException ex) {
			AnsiConsole.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Error while listing network interfaces!").reset());
		}
     }


    protected void displayNetworkInterface(NetworkInterface networkInterface) throws SocketException {
		//AnsiConsole.out.printf("Display name: %s\n", networkInterface.getDisplayName());
        AnsiConsole.out.printf(networkInterface.getName());

		byte[] mac = networkInterface.getHardwareAddress();
		if(mac != null) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mac.length; i++) {
				sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
			}
			AnsiConsole.out.printf(" " + sb.toString());
		} else {
			AnsiConsole.out.printf("                  ");
		}

        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        for(InetAddress inetAddress : Collections.list(inetAddresses)) {
			if(ONLY_IPV4_ADDRESSES && !(inetAddress instanceof Inet4Address)) {
				continue;
			}
            AnsiConsole.out.printf(" %s", inetAddress.getHostAddress());
        }

        AnsiConsole.out.printf("\n");
     }





	public static String cmdExec(String cmdLine) {
		String line;
		String output = "";
		try {
			Process p = Runtime.getRuntime().exec(cmdLine);
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				output += (line + '\n');
			}
			input.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return output;
	}
}
