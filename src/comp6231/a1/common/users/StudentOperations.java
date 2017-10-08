/**
 * 
 */
package comp6231.a1.common.users;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.TimeSlotResult;

/**
 * @author saman
 *
 */
public interface StudentOperations extends Remote {
	String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot) throws RemoteException, NotBoundException, IOException, InterruptedException;
	ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date) throws RemoteException, NotBoundException, IOException, InterruptedException;
	boolean cancelBooking(String user_id, String bookingID) throws RemoteException, NotBoundException, IOException, InterruptedException;	
}
