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
		logger.info("**********************************");
	}

	/* (non-Javadoc)
	 * @see comp6231.a1.common.users.StudentOperations#bookRoom(java.lang.String, java.lang.String, int, comp6231.a1.common.DateReservation, comp6231.a1.common.TimeSlot)
	 */
	public String bookRoom(String campus_name, int room_number, DateReservation date,
			TimeSlot time_slot) throws RemoteException, NotBoundException, IOException, InterruptedException {
		String log_msg = String.format("sending bookRoom(campus name: %s, room number: %d, date: %s, time slot: %s)", 
				campus_name, room_number, date, time_slot);
		logger.info(LoggerHelper.format(log_msg));
		String res = remote_stub.bookRoom(user.getUserId(), campus_name, room_number, date, time_slot);
		log_msg = String.format("bookRoom(campus name: %s, room number: %d, date: %s, time slot: %s): %s", 
				campus_name, room_number, date, time_slot, (res == null ? "null" : res));
		logger.info(LoggerHelper.format(log_msg));
		return res;
	}

	/* (non-Javadoc)
	 * @see comp6231.a1.common.users.StudentOperations#getAvailableTimeSlot(comp6231.a1.common.DateReservation)
	 */
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date)
			throws RemoteException, NotBoundException, IOException, InterruptedException {
		String log_msg = String.format("%s is sending getAvailableTimeSlot(date %s)", user.getUserId(), date);
		logger.info(LoggerHelper.format(log_msg));
		ArrayList<TimeSlotResult> res = remote_stub.getAvailableTimeSlot(date);
		log_msg = String.format("%s is sending getAvailableTimeSlot(date %s): %s", user.getUserId(), date, res);
		return res;
	}

	/* (non-Javadoc)
	 * @see comp6231.a1.common.users.StudentOperations#cancelBooking(java.lang.String, java.lang.String)
	 */
	public boolean cancelBooking(String bookingID)
			throws RemoteException, NotBoundException, IOException, InterruptedException {
		String log_msg = String.format("%s is sending cancelBooking(booking id: %s)", user.getUserId(), bookingID);
		logger.info(LoggerHelper.format(log_msg));
		boolean status = remote_stub.cancelBooking(user.getUserId(), bookingID);
		log_msg = String.format("%s is sending cancelBooking(booking id: %s)", user.getUserId(), bookingID);
		logger.info(LoggerHelper.format(log_msg));
		return status;
	}

}
