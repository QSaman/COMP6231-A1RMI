/**
 * 
 */
package comp6231.a1.campus;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.TimeSlotResult;
import comp6231.a1.common.users.AdminOperations;
import comp6231.a1.common.users.CampusUser;
import comp6231.a1.common.users.StudentOperations;

/**
 * @author saman
 *
 */
public class Campus implements AdminOperations, StudentOperations, CampusOperations, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private Registry registry;
	private HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db;
	private HashMap<String, StudentRecord> student_db;
	private static int booking_id_number = 0;
	private static final Object booking_id_number_lock = new Object();
	private String address;	//The address of this server
	private int port;	//The UDP listening port of this server
	private Object write_db_lock = new Object();
	private UdpServer udp_server;
	
	private abstract class DbOperations	//(key, vale), value = (sub_key, sub_value)
	{
		@SuppressWarnings("unused")
		private HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db;
		protected boolean operation_status;
		public DbOperations(HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db)
		{
			this.db = db;
			operation_status = true;
		}
		public abstract void onNullValue();
		public abstract void onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val);
		public abstract void onSubValue(HashMap<Integer, ArrayList<TimeSlot>> val, ArrayList<TimeSlot> sub_val);
		public abstract TimeSlot searchTimeSlot(ArrayList<TimeSlot> sub_val);
	}
	
	public Campus(String name, Registry registry, String address, int port) throws SocketException, RemoteException
	{
		this.name = name;
		this.registry = registry;
		db = new HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>>();
		student_db = new HashMap<String, StudentRecord>();
		this.address = address;
		this.port = port;
		udp_server = new UdpServer(this); 
		udp_server.start();
	}
	
	public void startRmiServer() throws RemoteException
	{
		System.setProperty("java.security.policy", "file:./src/comp6231/security.policy");
//		System.setProperty("java.rmi.server.codebase", "file:///media/NixHddData/MyStuff/Programming/Projects/Java/workspace/A1RMI/bin/");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		
		Remote stub = UnicastRemoteObject.exportObject(this, 0);
	
		registry.rebind(getName(), stub);
		System.out.println(getName() + " bound");
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

	}
	
	private TimeSlot traverseDb(int room_number, DateReservation date, ArrayList<TimeSlot> time_slots, DbOperations db_ops)
	{
		HashMap<Integer, ArrayList<TimeSlot>> val = null;
		TimeSlot ret = null;
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
				{
					db_ops.onSubValue(val, sub_val);
					ret = db_ops.searchTimeSlot(sub_val);
				}
			}
			System.out.println(db);
		}
		return ret;
	}

	@Override
	public boolean createRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		CampusUser user = new CampusUser(user_id);
		if (!user.isAdmin())
			return false;
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

			@Override
			public TimeSlot searchTimeSlot(ArrayList<TimeSlot> sub_val) { return null; }
		});
		return true;
	}

	//TODO reduce student count if the reservation is deleted
	@Override
	public boolean deleteRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		CampusUser user = new CampusUser(user_id);
		if (!user.isAdmin())
			return false;
		DbOperations ops = new DbOperations(db) {
			
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
			public void onNullValue() {operation_status = false;}
			
			@Override
			public void onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {operation_status = false;}

			@Override
			public TimeSlot searchTimeSlot(ArrayList<TimeSlot> sub_val) { return null; }
		};
		
		traverseDb(room_number, date, time_slots, ops);
		return ops.operation_status;
	}
	
	private String generateBookingId()
	{
		synchronized (booking_id_number_lock) {
			++booking_id_number;
		}
		return getName() + "#" + booking_id_number;
	}
	
	private TimeSlot isTimeSlotAvailable(int room_number, DateReservation date, TimeSlot time_slot)
	{
		DbOperations ops = new DbOperations(db) {
			
			@Override
			public void onSubValue(HashMap<Integer, ArrayList<TimeSlot>> val, ArrayList<TimeSlot> sub_val) {
			}
			
			@Override
			public void onNullValue() {}
			
			@Override
			public void onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {}

			@Override
			public TimeSlot searchTimeSlot(ArrayList<TimeSlot> sub_val) {
				TimeSlot ret = null;
				for (TimeSlot ts : sub_val)
					if (ts.equals(time_slot) && !ts.isBooked())
					{
						ret = ts;
						break;
					}
				return ret;
			}
		};
		ArrayList<TimeSlot> tmp = new ArrayList<TimeSlot>();
		tmp.add(time_slot);
		return traverseDb(room_number, date, tmp, ops);
	}
	
	private void sendMessage(byte[] message, String campus_name) throws NotBoundException, IOException
	{
		CampusOperations ops = (CampusOperations)registry.lookup(campus_name);
		InetAddress address = InetAddress.getByName(ops.getAddress());
		int port = ops.getPort();
		udp_server.sendDatagram(message, address, port);
	}

	@Override
	public String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot)
			throws RemoteException, NotBoundException, IOException, InterruptedException {
		CampusUser user = new CampusUser(user_id);
		String booking_id = null;
		if (!user.isStudent())
			return null;
		if (campus_name.equals(getName()))
		{
			StudentRecord record = student_db.get(user_id);
			if (record == null)
				record = new StudentRecord(user);
			record.removeOldRecords(date, time_slot);
			if (!record.canBook())
				return null;
			TimeSlot ts = isTimeSlotAvailable(room_number, date, time_slot);
			if (ts == null)
				return null;
			booking_id = generateBookingId();
			record.saveBookRoomRequest(booking_id, date, time_slot);
			student_db.put(user_id, record);
			ts.bookTimeSlot(user_id, booking_id);
			System.out.println(getName() + ": " + student_db);
			System.out.println(getName() + ": " + db);
		}
		else
		{
			int msg_id = MessageProtocol.generateMessageId();
			byte[] send_msg = MessageProtocol.encodeBookRoomMessage(msg_id, user_id, room_number, date, time_slot);
			sendMessage(send_msg, campus_name);
			UdpServer.BookRoomObject wait_object = udp_server.new BookRoomObject(); 
			udp_server.addToWaitList(msg_id, wait_object);
			synchronized (wait_object) {
				wait_object.wait();
			}			
			booking_id = wait_object.bookingId;
		}
		return booking_id;
	}

	@Override
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean cancelBooking(String user_id, String bookingID) throws RemoteException {
		CampusUser user = new CampusUser(user_id);
		if (!user.isStudent())
			return false;
		return true;		
	}

	@Override
	public void testMethod() throws RemoteException {
		System.out.println("I am test!");
		
	}

	@Override
	public int getPort() throws RemoteException {
		return port;
	}

	@Override
	public String getAddress() throws RemoteException {
		return address;
	}

}
