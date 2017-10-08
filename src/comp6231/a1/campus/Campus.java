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

import comp6231.a1.campus.UdpServer.WaitObject;
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
	private final Object date_db_lock = new Object();
	private final Object room_db_lock = new Object();
	private HashMap<String, StudentRecord> student_db;
	private final Object write_student_db_lock = new Object();		
	private String address;	//The address of this server
	private int port;	//The UDP listening port of this server	
	private UdpServer udp_server;
		
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
	
	@Override
	public String getCampusName() throws RemoteException {
		return getName();
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
	
	private abstract class DbOperations	//(key, vale), value = (sub_key, sub_value)
	{
		@SuppressWarnings("unused")
		private HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> _db;
		protected boolean _operation_status;
		protected TimeSlot _time_slot;
		protected ArrayList<TimeSlot> _time_slots;
		
		public DbOperations(HashMap<DateReservation, HashMap<Integer, ArrayList<TimeSlot>>> db)
		{
			this._db = db;
			_operation_status = true;
		}
		//All the following methods are thread-safe for campus time slots database
		public abstract HashMap<Integer, ArrayList<TimeSlot>> onNullValue();
		public abstract ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val);
		public abstract void onSubValue(ArrayList<TimeSlot> sub_val);
	}
	
	private void traverseDb(int room_number, DateReservation date, DbOperations db_ops)
	{
		HashMap<Integer, ArrayList<TimeSlot>> val = null;
		//https://stackoverflow.com/questions/11050539/using-hashmap-in-multithreaded-environment:
		//https://stackoverflow.com/questions/2688629/is-a-hashmap-thread-safe-for-different-keys:
		//The get() goes to an infinite loop because one of the threads has only a partially updated 
		//view of the HashMap in memory and there must be some sort of pointer loop
		synchronized (date_db_lock) {
			val = db.get(date);
			if (val == null)
			{
				val = db_ops.onNullValue();
				if (val == null)
					return;
				db.put(date, val);
			}
		}		

		ArrayList<TimeSlot> sub_val = null;
		synchronized (room_db_lock) {
			 sub_val = val.get(room_number);
			 if (sub_val == null)
			 {
				 sub_val = db_ops.onNullSubValue(val);
				 if (sub_val == null)
					 return;
				 val.put(room_number, sub_val);
			 }
		}
		synchronized (sub_val) {
			db_ops.onSubValue(sub_val);
		}				
		System.out.println(db);
	}

	@Override
	public boolean createRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		CampusUser user = new CampusUser(user_id);
		if (!user.isAdmin() || !user.getCampus().equals(getName()))
			return false;
		traverseDb(room_number, date, new DbOperations(db) {
			
			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {
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
			}
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {
				HashMap<Integer, ArrayList<TimeSlot>> val = new HashMap<Integer, ArrayList<TimeSlot>>();
				val.put(room_number, time_slots);
				return val;
			}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {				
				return time_slots;
			}
		});
		return true;
	}

	//TODO reduce student count if the reservation is deleted
	@Override
	public boolean deleteRoom(String user_id, int room_number, DateReservation date, ArrayList<TimeSlot> time_slots) throws RemoteException {
		CampusUser user = new CampusUser(user_id);
		if (!user.isAdmin() || !user.getCampus().equals(getName()))
			return false;
		DbOperations ops = new DbOperations(db) {
			
			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {
				ArrayList<Thread> threads = new ArrayList<>();
				HashMap<Integer, WaitObject> res = new HashMap<>();	//(message_id, wait ojbect)
				HashMap<Integer, TimeSlot> removed_list = new HashMap<>();	//(message_id, time slot)
				
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
					if (found)	//We need to delete val1 from time slot list
					{
						if (val1.isBooked())
						{
							CampusUser user = new CampusUser(val1.getUsername());
							if (userBelongHere(user))
							{
								boolean status = removeStudentRecord(val1.getUsername(), val1.getBookingId());
								if (!status)
								{
									_operation_status = false;
									System.out.println("Update student record (failed)");
								}
							}
							else
							{
								int message_id = MessageProtocol.generateMessageId();
								byte[] send_msg = MessageProtocol.encodeRemoveStudentRecordMessage(message_id, val1.getUsername(), val1.getBookingId());								
								try {
									UdpServer.WaitObject wait_object = udp_server.new WaitObject();
									udp_server.addToWaitList(message_id, wait_object);
									Thread thread = new Thread(new Runnable() {
										
										@Override
										public void run() {
											synchronized (wait_object) {
												try {
													wait_object.wait();
												} catch (InterruptedException e) {
													// TODO Auto-generated catch block
													e.printStackTrace();
												}
											}
										}
									});
									res.put(message_id, wait_object);
									removed_list.put(message_id, val1);
									thread.start();
									sendMessage(send_msg, user.getCampus());									
									threads.add(thread);
								} catch (NotBoundException | IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}
					}
					else	//We shouldn't delete val1
						new_time_slots.add(val1);
				}
				//Since we use sub_val as a lock object (see create room method), we couldn't simply use val.put(room_number, new_time_slots)
				sub_val.clear();
				for (TimeSlot value : new_time_slots)
					sub_val.add(value);
				
				try {
					for (Thread thread : threads)
						thread.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				for (int message_id : res.keySet())
				{
					TimeSlot ts = removed_list.get(message_id);
					boolean status = res.get(message_id).status;
					if (!status)
					{
						System.out.println("Cannot remove booking id (failed) " + ts.getBookingId() + " for user " + ts.getUsername());
						_operation_status = false;
					}
				}
			}
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {_operation_status = false; return null;}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {_operation_status = false; return null;}
		};
		
		traverseDb(room_number, date, ops);
		return ops._operation_status;
	}
	
	/**
	 * 
	 * @param room_number
	 * @param date
	 * @param time_slot
	 * @return TimeSlot or null if it's not available
	 */
	private TimeSlot findTimeSlot(int room_number, DateReservation date, TimeSlot time_slot)
	{
		DbOperations ops = new DbOperations(db) {
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {return null;}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) {return null;}

			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {
				for (TimeSlot ts : sub_val)
					if (ts.equals(time_slot))
					{
						_time_slot = ts;
						break;
					}		
			}
		};
		traverseDb(room_number, date, ops);
		return ops._time_slot;
	}
	
	private ArrayList<TimeSlot> findTimeSlots(int room_number, DateReservation date)
	{
		DbOperations ops = new DbOperations(db) {			
			
			@Override
			public void onSubValue(ArrayList<TimeSlot> sub_val) {_time_slots = sub_val;}
			
			@Override
			public HashMap<Integer, ArrayList<TimeSlot>> onNullValue() {return null;}
			
			@Override
			public ArrayList<TimeSlot> onNullSubValue(HashMap<Integer, ArrayList<TimeSlot>> val) { return null;}
		};
		traverseDb(room_number, date, ops);
		return ops._time_slots;
	}
	
	private void sendMessage(byte[] message, String campus_name) throws NotBoundException, IOException
	{
		CampusOperations ops = (CampusOperations)registry.lookup(campus_name);
		InetAddress address = InetAddress.getByName(ops.getAddress());
		int port = ops.getPort();
		udp_server.sendDatagram(message, address, port);
	}
	
	private boolean userBelongHere(CampusUser user)
	{
		return user.getCampus().equals(getName());
	}
	
	private abstract class UserDbOperations
	{
		protected CampusUser _user;
		public String _booking_id;
		@SuppressWarnings("unused")
		protected HashMap<String, StudentRecord> _student_db;
		protected boolean _status;
		
		public UserDbOperations(HashMap<String, StudentRecord> student_db, CampusUser user) 
		{
			this._user = user;
			this._student_db = student_db;
			_booking_id = null;
			_status = true;
		}
		
		//All the following methods are tread-safe for user database
		public abstract boolean onUserBelongHere(StudentRecord record);
		public abstract StudentRecord onNullUserRecord(CampusUser user);
		public abstract boolean onOperationOnThisCampus(ArrayList<TimeSlot> time_slots);
		public abstract void onOperationOnOtherCampus(int message_id) throws NotBoundException, IOException, InterruptedException;
		public abstract void onPostUserBelongHere(StudentRecord record);
		public abstract ArrayList<TimeSlot> findTimeSlots();
	}

	private void traverseStudentDb(UserDbOperations user_db_ops, CampusUser user, String campus_name) throws NotBoundException, IOException, InterruptedException
	{
		
		//String booking_id = null;
		StudentRecord record = null;
		
		if (userBelongHere(user))
		{
			synchronized (write_student_db_lock) {
				record = student_db.get(user.getUserId());
				if (record == null)
				{
					record = user_db_ops.onNullUserRecord(user);
					if (record == null)
						return;
					student_db.put(user.getUserId(), record);
				}				
			}
			synchronized (record) {			
				if (!user_db_ops.onUserBelongHere(record))
					return;
				
				if (campus_name.equals(getName()))
				{
					ArrayList<TimeSlot> time_slots = user_db_ops.findTimeSlots();
					if (time_slots == null)
						return;
					synchronized (time_slots) {
						if (!user_db_ops.onOperationOnThisCampus(time_slots))
							return;
					}
				}
				else
				{
					int msg_id = MessageProtocol.generateMessageId();
					user_db_ops.onOperationOnOtherCampus(msg_id);
				}
				user_db_ops.onPostUserBelongHere(record);
			}
		}
		else if (campus_name.equals(getName()))	//We received booking request from another campus
		{
			ArrayList<TimeSlot> time_slots = user_db_ops.findTimeSlots();
			if (time_slots == null)
				return;
			synchronized (time_slots) {
				user_db_ops.onOperationOnThisCampus(time_slots);
			}			
		}
		else
			throw new IllegalArgumentException("The sender campus send the message to the wrong campus: (" + campus_name + ", " + getName() + ")");
	}
	
	@Override
	public String bookRoom(String user_id, String campus_name, int room_number, DateReservation date, TimeSlot time_slot)
			throws RemoteException, NotBoundException, IOException, InterruptedException {
		
		CampusUser user = new CampusUser(user_id);		
		if (!user.isStudent())
			return null;
		
		UserDbOperations ops = new UserDbOperations(student_db, user) {
			
			@Override
			public boolean onUserBelongHere(StudentRecord record) {
				if (!record.canBook())
					return false;
				return true;
			}
			
			@Override
			public void onPostUserBelongHere(StudentRecord record) {
				record.saveBookRoomRequest(_booking_id, date, time_slot);
				synchronized (write_student_db_lock) {
					_student_db.put(user_id, record);
				}				
			}
			
			@Override
			public StudentRecord onNullUserRecord(CampusUser user)
			{
				StudentRecord record = new StudentRecord(user);
				return record;
			}
			
			@Override
			public boolean onOperationOnThisCampus(ArrayList<TimeSlot> time_slots) {									
				if (time_slots == null)
					return false;
				TimeSlot ts = null;
				for (TimeSlot tmp : time_slots)
					if (tmp.equals(time_slot) && !tmp.isBooked())
					{
						ts = tmp;
						break;
					}
				if (ts == null)
					return false;
				_booking_id = BookingIdGenerator.generate(getName(), date, room_number);
				ts.bookTimeSlot(user_id, _booking_id);
				System.out.println(getName() + ": " + _student_db);
				System.out.println(getName() + ": " + db);
				return true;
			}
			
			@Override
			public void onOperationOnOtherCampus(int message_id) throws NotBoundException, IOException, InterruptedException {
				int msg_id = MessageProtocol.generateMessageId();
				byte[] send_msg = MessageProtocol.encodeBookRoomMessage(msg_id, user_id, room_number, date, time_slot);				
				UdpServer.WaitObject wait_object = udp_server.new WaitObject(); 
				udp_server.addToWaitList(msg_id, wait_object);				
				synchronized (wait_object) {
					sendMessage(send_msg, campus_name);
					wait_object.wait();
				}			
				_booking_id = wait_object.bookingId;
				
			}
			
			@Override
			public ArrayList<TimeSlot> findTimeSlots()
			{
				return Campus.this.findTimeSlots(room_number, date);
			}
		};
		
		traverseStudentDb(ops, user, campus_name);			
		return ops._booking_id;
	}
	
	public int getThisCampusAvailableTimeSlots(DateReservation date)
	{
		HashMap<Integer, ArrayList<TimeSlot>> val = null;
		synchronized (date_db_lock) {
			val = db.get(date);
		}
		int ret = 0;
		if (val == null)
			return ret;
		synchronized (room_db_lock) {
			for (int room_number : val.keySet())
			{
				ArrayList<TimeSlot> time_slots = val.get(room_number);
				for (TimeSlot time_slot : time_slots)
					if (!time_slot.isBooked())
						++ret;
			}
		}
		return ret;
	}	

	@Override
	public ArrayList<TimeSlotResult> getAvailableTimeSlot(DateReservation date) throws RemoteException, NotBoundException, IOException, InterruptedException {
		ArrayList<TimeSlotResult> ret = new ArrayList<TimeSlotResult>();
		ret.add(new TimeSlotResult(getName(), getThisCampusAvailableTimeSlots(date)));
		HashMap<Integer, String> hm = new HashMap<>();	//(message_id, campus_name)
		HashMap<Integer, UdpServer.WaitObject> hm_wo = new HashMap<>();	//(message_id, WaitObject)
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		for (String campus_str : registry.list())
		{
			CampusOperations ops = (CampusOperations)registry.lookup(campus_str);
			String campus_name = ops.getCampusName();
			if (campus_name.equals(getName()))
				continue;
			int message_id = MessageProtocol.generateMessageId();
			byte[] send_msg = MessageProtocol.encodeGetAvailableTimeSlotsMessage(message_id, date);
			hm.put(message_id, campus_name);			
			UdpServer.WaitObject wait_object = udp_server.new WaitObject();
			udp_server.addToWaitList(message_id, wait_object);			
			hm_wo.put(message_id, wait_object);
			Thread thread = new Thread(new Runnable() {				
				@Override
				public void run() {
					try {
						synchronized (wait_object) {
							wait_object.wait();
						}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}					
				}
			});
			thread.start();
			sendMessage(send_msg, campus_name);
			threads.add(thread);
		}
		System.out.println("threads.length(): " + threads.size()); 
		for (Thread thread : threads)
			thread.join();
		for (int msg_id : hm.keySet())
			ret.add(new TimeSlotResult(hm.get(msg_id), hm_wo.get(msg_id).available_timeslots));
		return ret;
	}
	
	public boolean removeStudentRecord(String user_id, String booking_id)
	{
		StudentRecord record = null;
		synchronized (write_student_db_lock) {
			record = student_db.get(user_id);
		}
		if (record == null)
			return false;
		synchronized (record) {
			return record.removeBookRoomRequest(booking_id);
		}
	}

	@Override
	public boolean cancelBooking(String user_id, String bookingID) throws RemoteException, NotBoundException, IOException, InterruptedException {
		CampusUser user = new CampusUser(user_id);
		if (!user.isStudent())
			return false;
		BookingIdGenerator big = new BookingIdGenerator(bookingID);
		UserDbOperations ops = new UserDbOperations(student_db, user) {
			
			@Override
			public boolean onUserBelongHere(StudentRecord record) {
				return true;
			}
			
			@Override
			public void onPostUserBelongHere(StudentRecord record) {
				_status = record.removeBookRoomRequest(bookingID);
			}
			
			@Override
			public boolean onOperationOnThisCampus(ArrayList<TimeSlot> time_slots) {
				for (TimeSlot time_slot : time_slots)
					if (time_slot.getBookingId().equals(bookingID) && time_slot.getUsername().equals(user_id))
					{
						time_slot.cancelTimeSlot();
						return true;
					}
				_status = false;
				return _status;
			}
			
			@Override
			public void onOperationOnOtherCampus(int message_id) throws NotBoundException, IOException, InterruptedException {
				int msg_id = MessageProtocol.generateMessageId();
				byte[] send_msg = MessageProtocol.encodeCancelBookRoomMessage(msg_id, user_id, bookingID);				
				UdpServer.WaitObject wait_object = udp_server.new WaitObject(); 
				udp_server.addToWaitList(msg_id, wait_object);
				synchronized (wait_object) {
					sendMessage(send_msg, big.getCampusName());
					wait_object.wait();
				}
				_status = wait_object.status;
				
			}
			
			@Override
			public StudentRecord onNullUserRecord(CampusUser user) {_status = false; return null;}
			
			@Override
			public ArrayList<TimeSlot> findTimeSlots() {				
				ArrayList<TimeSlot> time_slots = Campus.this.findTimeSlots(big.getRoomNumber(), big.getDate());
				if (time_slots == null)
					_status = false;
				return time_slots;
			}
		};
		traverseStudentDb(ops, user, big.getCampusName());
		return ops._status;		
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
