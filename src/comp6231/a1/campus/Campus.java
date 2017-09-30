/**
 * 
 */
package comp6231.a1.campus;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import comp6231.a1.common.AdminUser;
import comp6231.a1.common.DateReservation;
import comp6231.a1.common.StudentUser;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.TimeSlotResult;

/**
 * @author saman
 *
 */
public class Campus implements AdminUser, StudentUser, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db;
	
	public Campus(String name)
	{
		this.name = name;
		db = new HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param args
	 * @throws RemoteException 
	 */
	public static void main(String[] args) throws RemoteException {
		System.setProperty("java.security.policy", "file:./src/comp6231/security.policy");
//		System.setProperty("java.rmi.server.codebase", "file:///media/NixHddData/MyStuff/Programming/Projects/Java/workspace/A1RMI/bin/");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		String camp_name = "saman";//args[0];
		Campus campus = new Campus(camp_name);
		
		Remote stub = UnicastRemoteObject.exportObject(campus, 0);

		
		Registry registry = LocateRegistry.createRegistry(1099);
		System.out.println("Java RMI registry created.");
		registry.rebind(camp_name + "_admin", (AdminUser)stub);
		registry.rebind(camp_name + "_student", (StudentUser)stub);
		System.out.println(camp_name + " bound");
	}

	@Override
	public void createRoom(int room_number, DateReservation date, ArrayList<TimeSlot> time_intervals) throws RemoteException {
		HashMap<Integer, ArrayList<TimeSlot>> val = db.get(date);
		if (val == null)
		{
			val = new HashMap<Integer, ArrayList<TimeSlot>>();
			val.put(room_number, time_intervals);
			db.put(date, val);
		}
		else
		{
			ArrayList<TimeSlot> sub_val = val.get(room_number);
			if (sub_val == null)
				val.put(room_number, time_intervals);
			else
			{
				for (TimeSlot time_slot : time_intervals)
				{
					boolean conflict = false;
					for (TimeSlot cur : sub_val)
						if (cur.conflict(time_slot))
						{
							conflict = true;
							break;
						}
					if (!conflict)
						sub_val.add(time_slot);
				}
				val.put(room_number, sub_val);
			}
			db.put(date, val);
		}
		System.out.println(db);
	}

	@Override
	public void deleteRoom(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookRoom(String campus_name, int room_number, DateReservation date, TimeSlot time_slot)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelBooking(String bookingID) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void testMethod() throws RemoteException {
		System.out.println("I am test!");
		
	}

}
