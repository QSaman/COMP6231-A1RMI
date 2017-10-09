package comp6231.a1.users;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Logger;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.LoggerHelper;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.users.AdminOperations;
import comp6231.a1.common.users.CampusUser;

/**
 * @author saman
 *
 */
public class AdminClient {
	
	private Logger logger;
	private CampusUser user;
	AdminOperations remote_stub;
	
	public AdminClient(CampusUser user, Logger logger, AdminOperations remote_stub) {
		this.user = user;
		this.logger = logger;
		this.remote_stub = remote_stub;
		logger.info("**********************************");
	}

	public boolean createRoom(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots)
			throws RemoteException {
		String log_msg = String.format("%s sending createRoom(%d, %s, %s)", user.getUserId(), room_number, date, time_slots);
		logger.info(LoggerHelper.format(log_msg));
		boolean status = remote_stub.createRoom(user.getUserId(), room_number, date, time_slots);
		log_msg = String.format("%s createRoom(%d, %s, %s): %s", user.getUserId(), room_number, date, time_slots, status);
		logger.info(LoggerHelper.format(log_msg));
		return status;
	}

	public boolean deleteRoom(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots)
			throws RemoteException {
		String log_msg = String.format("%s sending deleteRoom(%d, %s, %s)", user.getUserId(), room_number, date, time_slots);
		logger.info(LoggerHelper.format(log_msg));
		boolean status = remote_stub.deleteRoom(user.getUserId(), room_number, date, time_slots);
		log_msg = String.format("%s deleteRoom(%d, %s, %s): %s", user.getUserId(), room_number, date, time_slots, status);
		return status;
	}

	public void startWeek() throws RemoteException {
		String log_msg = String.format("%s sending startWeek", user.getUserId());
		logger.info(LoggerHelper.format(log_msg));
		remote_stub.startWeek();		
	}

	public void testMethod() throws RemoteException {
		remote_stub.testMethod();		
	}

}
