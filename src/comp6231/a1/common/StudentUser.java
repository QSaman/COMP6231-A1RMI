/**
 * 
 */
package comp6231.a1.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * @author saman
 *
 */
public interface StudentUser extends Remote {
	
	void bookRoom(String campus_name, int room_number, DateReservation date, TimeSlot time_slot) throws RemoteException;
	ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date) throws RemoteException;
	void cancelBooking(String bookingID) throws RemoteException;

}
