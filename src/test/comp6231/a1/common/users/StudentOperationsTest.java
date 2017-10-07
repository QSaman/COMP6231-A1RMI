package test.comp6231.a1.common.users;

import static org.junit.Assert.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.TimeSlotResult;
import comp6231.a1.common.users.StudentOperations;

public class StudentOperationsTest {
	
	class Student
	{
		public String student_id;
		public String booking_id;
		public Student(String student_id, String booking_id)
		{
			this.student_id = student_id;
			this.booking_id = booking_id;
		}		
	}
	
	private static ArrayList<Student> booking_list_dvl = null;
	
	private static Registry registry;
	private static boolean cancel_booking_dvl = false;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("java.security.policy", "file:./src/comp6231/security.policy");
		if (System.getSecurityManager() == null)
			System.setSecurityManager(new SecurityManager());
		registry = LocateRegistry.getRegistry();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testBookRoomDVL() throws RemoteException, NotBoundException, IOException, InterruptedException {
		if (booking_list_dvl != null)
		{
			assertTrue(true);
			return;
		}
		booking_list_dvl = new ArrayList<Student>();
		StudentOperations user = (StudentOperations)registry.lookup("DVL");
		String booking_id = user.bookRoom("DVLS1111", "DVL", 777, new DateReservation("17-09-2017"), new TimeSlot("09:15 - 10:15"));
		assertNotNull(booking_id);
		booking_list_dvl.add(new Student("DVLS1111", booking_id));
		booking_id = user.bookRoom("DVLS1111", "DVL", 777, new DateReservation("17-09-2017"), new TimeSlot("09:15 - 10:15"));
		assertNull(booking_id);
		booking_id = user.bookRoom("DVLS1111", "DVL", 777, new DateReservation("17-09-2017"), new TimeSlot("12:15 - 12:16"));
		assertNull(booking_id);
		booking_id = user.bookRoom("DVLS1111", "KKL", 778, new DateReservation("18-09-2017"), new TimeSlot(8, 0, 10, 0));
		assertNotNull(booking_id);
		booking_list_dvl.add(new Student("DVLS1111", booking_id));
		booking_id = user.bookRoom("DVLS1111", "KKL", 778, new DateReservation("18-09-2017"), new TimeSlot(8, 0, 10, 0));
		assertNull("This time slot is reserved before!", booking_id);
		booking_id = user.bookRoom("DVLA1111", "KKL", 778, new DateReservation("18-09-2017"), new TimeSlot(13, 0, 17, 15));
		assertNull("admin cannot book a room!", booking_id);
		booking_id = user.bookRoom("DVLS1112", "KKL", 778, new DateReservation("18-09-2017"), new TimeSlot(13, 0, 17, 15));
		booking_list_dvl.add(new Student("DVLS1112", booking_id));
		booking_id = user.bookRoom("DVLS1113", "WST", 779, new DateReservation("19-09-2017"), new TimeSlot(18, 0, 19, 0));
		assertNotNull(booking_id);
		booking_list_dvl.add(new Student("DVLS1113", booking_id));
		booking_id = user.bookRoom("DVLS1113", "WST", 779, new DateReservation("19-09-2017"), new TimeSlot(19, 0, 20, 0));
		assertNotNull(booking_id);
		booking_list_dvl.add(new Student("DVLS1113", booking_id));
	}

	@Test
	public final void testGetAvailableTimeSlot() throws RemoteException, NotBoundException, IOException, InterruptedException {
		if (booking_list_dvl == null)
			testBookRoomDVL();
		StudentOperations user = (StudentOperations)registry.lookup("DVL");
		ArrayList<TimeSlotResult> res = user.getAvailableTimeSlot(new DateReservation("19-09-2017"));
		assertTrue(res.size() == 3);
	}

	@Test
	public final void testCancelBookingDVL() throws RemoteException, NotBoundException, IOException, InterruptedException {
		if (cancel_booking_dvl)
		{
			assertTrue(true);
			return;
		}
		cancel_booking_dvl = true;
		if (booking_list_dvl == null)
			testBookRoomDVL();
		StudentOperations user = (StudentOperations)registry.lookup("DVL");
		boolean status = user.cancelBooking("DVLS1111", "DVL@17-9-2017@777#999999");
		assertFalse("This booking id is not valid", status);
		status = user.cancelBooking(booking_list_dvl.get(0).student_id, booking_list_dvl.get(0).booking_id);
		assertTrue(status);
		status = user.cancelBooking(booking_list_dvl.get(4).student_id, booking_list_dvl.get(4).booking_id);
		assertTrue(status);
		status = user.cancelBooking(booking_list_dvl.get(4).student_id, booking_list_dvl.get(4).booking_id);
		assertFalse(status);
	}

}
