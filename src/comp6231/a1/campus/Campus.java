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
	private Object write_db_lock = new Object();
	private abstract class DbOperations	//(key, vale), value = (sub_key, sub_value)
	{
		@SuppressWarnings("unused")
		private HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db;
		public DbOperations(HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db)
		{
			this.db = db;
		}
		public abstract void onNullValue();
		public abstract void onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val);
		public abstract void onSubValue(HashMap<Integer, ArrayList<TimeSlot>> val, ArrayList<TimeSlot> sub_val);
	}
	
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
	
	private void traverseDb(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots, DbOperations db_ops)
	{
		HashMap<Integer, ArrayList<TimeSlot>> val = null;
		synchronized (write_db_lock) {
			val = db.get(date);
			if (val == null)
				db_ops.onNullValue();
			else
			{
				ArrayList<TimeSlot> sub_val = val.get(room_number);
				if (sub_val == null)
					db_ops.onNullSubValue(val);
				else
					db_ops.onSubValue(val, sub_val);
			}
			System.out.println(db);
		}
	}

	@Override
	public void createRoom(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		traverseDb(room_number, date, time_slots, new DbOperations(db) {
			
			@Override
			public void onSubValue(HashMap<Integer, ArrayList<TimeSlot>> val, ArrayList<TimeSlot> sub_val) {
				for (TimeSlot time_slot : time_slots)
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
				db.put(date, val);
			}
			
			@Override
			public void onNullValue() {
				HashMap<Integer, ArrayList<TimeSlot>> val = new HashMap<Integer, ArrayList<TimeSlot>>();
				val.put(room_number, time_slots);
				db.put(date, val);
			}
			
			@Override
			public void onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {
				val.put(room_number, time_slots);
				db.put(date, val);
			}
		});
	}

	@Override
	public void deleteRoom(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		traverseDb(room_number, date, time_slots, new DbOperations(db) {
			
			@Override
			public void onSubValue(HashMap<Integer, ArrayList<TimeSlot>> val, ArrayList<TimeSlot> sub_val) {
				ArrayList<TimeSlot> new_time_slots = new ArrayList<TimeSlot>(); 
				for (TimeSlot val1 : sub_val)
				{
					boolean found = false;
					for (TimeSlot val2 : time_slots)
						if (val1.equals(val2))
						{
							found = true;
							break;
						}
					if (!found)
						new_time_slots.add(val1);
				}
				val.put(room_number, new_time_slots);
				db.put(date, val);				
			}
			
			@Override
			public void onNullValue() {}
			
			@Override
			public void onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {}
		});			
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
