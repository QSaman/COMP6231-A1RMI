/**
 * 
 */
package test.comp6231.a1.users;

import static org.junit.Assert.*;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import comp6231.a1.campus.Bootstrap;
import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.users.AdminOperations;
import comp6231.a1.common.users.CampusUser;
import comp6231.a1.users.AdminClient;
import comp6231.a1.users.ClientUserFactory;

/**
 * @author saman
 *
 */
public class AdminClientTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Bootstrap.initServers();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link comp6231.a1.users.AdminClient#createRoom(int, comp6231.a1.common.DateReservation, java.util.ArrayList)}.
	 * @throws NotBoundException 
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	@Test
	public final void testCreateRoomDVL() throws SecurityException, IOException, NotBoundException {
		AdminClient dvla1111 = ClientUserFactory.createAdminClient(new CampusUser("DVLA1111"));
		ArrayList<TimeSlot> time_slots = new ArrayList<TimeSlot>();
		time_slots.add(new TimeSlot(7, 1, 8, 15));
		time_slots.add(new TimeSlot(9, 15, 10, 15));
		DateReservation date = new DateReservation("17-09-2017");
		int room_number = 777;
		boolean res = dvla1111.createRoom(room_number, date, time_slots);
		assertTrue(res);
		time_slots.add(new TimeSlot(11, 0, 14, 55));
		res = dvla1111.createRoom(room_number, date, time_slots);
		assertTrue(res);
	}
	
	/**
	 * 
	 * @throws SecurityException
	 * @throws IOException
	 * @throws NotBoundException
	 */
	@Test
	public final void testCreateRoomKKL() throws SecurityException, IOException, NotBoundException {
		AdminClient kkla1111 = ClientUserFactory.createAdminClient(new CampusUser("KKLA1111"));
		ArrayList<TimeSlot> time_slots = new ArrayList<TimeSlot>();
		time_slots.add(new TimeSlot(8, 0, 10, 0));
		time_slots.add(new TimeSlot(10, 15, 11, 15));
		time_slots.add(new TimeSlot(11, 15, 12, 15));
		time_slots.add(new TimeSlot(13, 0, 17, 15));
		DateReservation date = new DateReservation("18-09-2017");
		int room_number = 778;
		boolean res = kkla1111.createRoom(room_number, date, time_slots);
		assertTrue(res);
	}
	
	@Test
	public final void testCreateRoomWST() throws SecurityException, IOException, NotBoundException {
		AdminClient wsta1111 = ClientUserFactory.createAdminClient(new CampusUser("WSTA1111"));
		ArrayList<TimeSlot> time_slots = new ArrayList<TimeSlot>();
		time_slots.add(new TimeSlot(14, 0, 15, 0));
		time_slots.add(new TimeSlot(15, 0, 16, 0));
		time_slots.add(new TimeSlot(16, 0, 17, 0));
		time_slots.add(new TimeSlot(17, 0, 18, 0));
		time_slots.add(new TimeSlot(18, 0, 19, 0));
		time_slots.add(new TimeSlot(19, 0, 20, 0));
		DateReservation date = new DateReservation("19-09-2017");
		int room_number = 779;
		boolean res = wsta1111.createRoom(room_number, date, time_slots);
		assertTrue(res);		
	}

	/**
	 * Test method for {@link comp6231.a1.users.AdminClient#deleteRoom(int, comp6231.a1.common.DateReservation, java.util.ArrayList)}.
	 * @throws NotBoundException 
	 * @throws IOException 
	 * @throws SecurityException 
	 */
	@Test
	public final void testDeleteRoomDVL() throws SecurityException, IOException, NotBoundException {
		AdminClient dvla1111 = ClientUserFactory.createAdminClient(new CampusUser("DVLA1111"));
		dvla1111.startWeek();
		testCreateRoomDVL();
		
		DateReservation date = new DateReservation("17-09-2017");
		int room_number = 777;
		ArrayList<TimeSlot> time_slots = new ArrayList<TimeSlot>();
		time_slots.add(new TimeSlot(7, 1, 8, 15));
		boolean res = dvla1111.deleteRoom(room_number, date, time_slots);
		assertTrue(res);
	}

	/**
	 * Test method for {@link comp6231.a1.users.AdminClient#startWeek()}.
	 */
	@Test
	public final void testStartWeek() {
		//fail("Not yet implemented"); // TODO
	}

}
