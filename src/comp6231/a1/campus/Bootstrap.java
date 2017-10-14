/**
 * 
 */
package comp6231.a1.campus;

import java.io.IOException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.logging.Logger;

import comp6231.a1.common.LoggerHelper;

/**
 * @author saman
 *
 */
public class Bootstrap {
	public static ArrayList<Campus> campuses = new ArrayList<Campus>();
	public static boolean init = false;
	//If you set the following variable to true, run rmiregistry command from your bin directory
	public final static boolean different_processes = false;
	
	public static synchronized void initServers() throws SecurityException, IOException
	{
		if (init)
			return;
		init = true;
		String[] campus_names = {"DVL", "KKL", "WST"};
		int[] ports = {7777, 7778, 7779};
		if (!different_processes)
		{
			LocateRegistry.createRegistry(1099);
			System.out.println("Java RMI registry created.");
			initServers(campus_names, ports);
		}
	}
	
	public static synchronized void initServers(String[] campus_names, int[] ports) throws SecurityException, IOException {
		Registry registry = LocateRegistry.getRegistry();
		for (int i = 0; i < campus_names.length; ++i)
		{
			String campus_name = campus_names[i];
			int port = ports[i];
			Logger logger = LoggerHelper.getCampusServerLogger(campus_name);
			Campus campus = new Campus(campus_name, registry, "127.0.0.1", port, logger);
			campus.startRmiServer();
			campuses.add(campus);
		}
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	public static void main(String[] args) throws SecurityException, IOException {
		if (args.length == 0)
			initServers();
		else if (args.length == 2)
		{
			String[] campus_names = new String[1];
			campus_names[0] = args[0].trim();
			int[] ports = new int[1];
			ports[0] = Integer.parseInt(args[1].trim());
			initServers(campus_names, ports);
		}
	}

}
