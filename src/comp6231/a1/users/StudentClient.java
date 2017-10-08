/**
 * 
 */
package comp6231.a1.users;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.LoggerHelper;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.TimeSlotResult;
import comp6231.a1.common.users.CampusUser;
import comp6231.a1.common.users.StudentOperations;

/**
 * @author saman
 *
 */
public class StudentClient {
	
	private StudentOperations remote_stub;
	private Logger logger;
	private CampusUser user;
	
	public StudentClient(CampusUser user, Logger logger, StudentOperations remote_stub)
	{
		this.remote_stub = remote_stub;
		this.logger = logger;
		this.user = user;
	}

	/* (non-Javadoc)
	 * @see comp6231.a1.common.users.StudentOperations#bookRoom(java.lang.String, java.lang.String, int, comp6231.a1.common.DateReservation, comp6231.a1.common.TimeSlot)
	 */
	public String bookRoom(String campus_name, int room_number, DateReservation date,
			TimeSlot time_slot) throws RemoteException, NotBoundException, IOException, InterruptedException {
		String log_str = new String();
		log_str = user.getUserId() + " sending bookRoom(" + campus_name + ", " + room_number + ", " + date + ", " + time_slot;
		return remote_stub.bookRoom(user.getUserId(), campus_name, room_number, date, time_slot);
	}

	/* (non-Javadoc)
	 * @see comp6231.a1.common.users.StudentOperations#getAvailableTimeSlot(comp6231.a1.common.DateReservation)
	 */
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date)
			throws RemoteException, NotBoundException, IOException, InterruptedException {

		return remote_stub.getAvailableTimeSlot(date);
	}

	/* (non-Javadoc)
	 * @see comp6231.a1.common.users.StudentOperations#cancelBooking(java.lang.String, java.lang.String)
	 */
	public boolean cancelBooking(String bookingID)
			throws RemoteException, NotBoundException, IOException, InterruptedException {
		
		return remote_stub.cancelBooking(user.getUserId(), bookingID);
	}

}
