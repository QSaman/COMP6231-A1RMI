/**
 * 
 */
package test.comp6231.a1.users;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import comp6231.a1.campus.Bootstrap;
import comp6231.a1.users.AdminClient;

/**
 * @author saman
 *
 */
public class StudentClientTest {
	
	private AdminClientTest admin_test;
	
	public StudentClientTest() {
		admin_test = new AdminClientTest();
	}

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
		admin_test.testCreateRoomDVL();
		admin_test.testCreateRoomKKL();
		admin_test.testCreateRoomWST();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		admin_test.testStartWeek();
	}

	/**
	 * Test method for {@link comp6231.a1.users.StudentClient#bookRoom(java.lang.String, int, comp6231.a1.common.DateReservation, comp6231.a1.common.TimeSlot)}.
	 */
	@Test
	public final void testBookRoom() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link comp6231.a1.users.StudentClient#getAvailableTimeSlot(comp6231.a1.common.DateReservation)}.
	 */
	@Test
	public final void testGetAvailableTimeSlot() {
		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link comp6231.a1.users.StudentClient#cancelBooking(java.lang.String)}.
	 */
	@Test
	public final void testCancelBooking() {
		fail("Not yet implemented"); // TODO
	}

}
