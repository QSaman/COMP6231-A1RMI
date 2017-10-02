/**
 * 
 */
package comp6231.a1.campus;

import java.net.SocketException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

/**
 * @author saman
 *
 */
public class Bootstrap {
	public static ArrayList<Campus> campuses = new ArrayList<Campus>();

	/**
	 * @param args
	 * @throws RemoteException 
	 * @throws SocketException 
	 */
	public static void main(String[] args) throws RemoteException, SocketException {
		Registry registry = LocateRegistry.createRegistry(1099);
		System.out.println("Java RMI registry created.");
		String[] campus_names = {"DVL", "KKL", "WST"};
		int port = 7777;
		for (String campus_name : campus_names)
		{
			Campus campus = new Campus(campus_name, registry, "127.0.0.1", port++);
			campus.startRmiServer();
			campuses.add(campus);
		}
	}

}
