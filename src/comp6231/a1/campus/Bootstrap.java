/**
 * 
 */
package comp6231.a1.campus;

import java.io.IOException;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import comp6231.a1.common.LoggerHelper;

/**
 * @author saman
 *
 */
public class Bootstrap {
	public static ArrayList<Campus> campuses = new ArrayList<Campus>();
	public static boolean init = false;
	
	public static synchronized void initServers() throws SecurityException, IOException
	{
		if (init)
			return;
		init = true;
		Registry registry = LocateRegistry.createRegistry(1099);
		System.out.println("Java RMI registry created.");
		String[] campus_names = {"DVL", "KKL", "WST"};
		int port = 7777;
		for (String campus_name : campus_names)
		{
			Logger logger = LoggerHelper.getCampusServerLogger(campus_name);
			Campus campus = new Campus(campus_name, registry, "127.0.0.1", port++, logger);
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
//		Logger logger = Logger.getGlobal();
//		try {
//			FileHandler fh = new FileHandler("saman.log");
//			fh.setFormatter(new SimpleFormatter());
//			logger.addHandler(fh);
//		} catch (SecurityException | IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		logger.log(Level.INFO, "Hello World from logger");
//		logger.log(Level.INFO, "Revan was power: " + LoggerHelper.now());
		
		initServers();
	}

}
