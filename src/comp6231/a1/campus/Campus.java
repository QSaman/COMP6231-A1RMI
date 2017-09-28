/**
 * 
 */
package comp6231.a1.campus;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Calendar;

import comp6231.a1.common.AdminUser;
import comp6231.a1.common.StudentUser;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.TimeSlotResult;

/**
 * @author saman
 *
 */
public class Campus implements AdminUser, StudentUser {
	private String name;
	
	public Campus(String name)
	{
		this.name = name;
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
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		String camp_name = "saman";//args[0];
		Campus campus = new Campus(camp_name);
		
		Remote stub = UnicastRemoteObject.exportObject(campus, 0);
		
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(camp_name + "_admin", (AdminUser)stub);
		registry.rebind(camp_name + "_student", (StudentUser)stub);
		System.out.println(camp_name + " bound");
	}

	@Override
	public void createRoom(int room_number, Calendar date, ArrayList<TimeSlot> time_intervals) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteRoom(int room_number, Calendar date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void bookRoom(String campus_name, int room_number, Calendar date, TimeSlot time_slot)
			throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(Calendar date) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelBooking(String bookingID) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
