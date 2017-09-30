/**
 * 
 */
package comp6231.a1.users;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Calendar;

import comp6231.a1.common.AdminUser;
import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;

/**
 * @author saman
 *
 */

//int room_number, Calendar date, ArrayList<TimeSlot> time_slots
public class User {

	/**
	 * @param args
	 * @throws RemoteException 
	 * @throws NotBoundException 
	 */
	public static void main(String[] args) throws RemoteException, NotBoundException {
		System.setProperty("java.security.policy", "file:./src/comp6231/security.policy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		Registry registry = LocateRegistry.getRegistry();
		AdminUser user = (AdminUser)registry.lookup("saman_admin");
		test(user);
	}
	
	public static void test(AdminUser user) throws RemoteException
	{
		user.testMethod();
		ArrayList<TimeSlot> time_slots = new ArrayList<TimeSlot>();
		time_slots.add(new TimeSlot(7, 1, 8, 15));
		time_slots.add(new TimeSlot(9, 15, 10, 15));
		testCreateRoom(user, time_slots);
		time_slots.add(new TimeSlot(11, 0, 14, 55));
		testCreateRoom(user, time_slots);
		testDeleteRoom(user);
		
	}
	
	public static void testCreateRoom(AdminUser user, ArrayList<TimeSlot> time_slots)
	{
		DateReservation date = new DateReservation("17-09-2017");
		int room_number = 777;
		try {
			user.createRoom(room_number, date, time_slots);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void testDeleteRoom(AdminUser user)
	{
		DateReservation date = new DateReservation("17-09-2017");
		int room_number = 777;
		ArrayList<TimeSlot> time_slots = new ArrayList<TimeSlot>();
		time_slots.add(new TimeSlot(7, 1, 8, 15));
		try {
			user.deleteRoom(room_number, date, time_slots);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
