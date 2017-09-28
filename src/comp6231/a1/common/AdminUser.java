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
public interface AdminUser extends Remote {
	
	void createRoom(int room_number, Calendar date, ArrayList<TimeSlot> time_slots) throws RemoteException;
	void deleteRoom(int room_number, Calendar date, ArrayList<TimeSlot> time_slots) throws RemoteException;

}
