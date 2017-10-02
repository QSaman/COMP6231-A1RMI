/**
 * 
 */
package comp6231.a1.campus;

import java.util.ArrayList;

import comp6231.a1.common.DateReservation;
import comp6231.a1.common.TimeSlot;
import comp6231.a1.common.users.CampusUser;

/**
 * @author saman
 *
 */
public class StudentRecord {	
	
	private class ReservationRecord
	{
		private DateReservation date;
		private TimeSlot time_slot;
		private String booking_id;
				
		
		public ReservationRecord(String booking_id, DateReservation date, TimeSlot time_slot)
		{
			this.date = date;
			this.time_slot = time_slot;
			this.booking_id = booking_id;
		}

		/**
		 * @return the date
		 */
		public DateReservation getDate() {
			return date;
		}

		/**
		 * @return the time_slot
		 */
		public TimeSlot getTime_slot() {
			return time_slot;
		}

		/**
		 * @return the booking_id
		 */
		public String getBooking_id() {
			return booking_id;
		}		
	}
	
	ArrayList<ReservationRecord> records;
	private CampusUser user;
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((user == null) ? 0 : user.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof StudentRecord))
			return false;
		StudentRecord other = (StudentRecord) obj;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return user.getUserId() + ", canBook?" + canBook();
	}

	public StudentRecord(CampusUser user)
	{
		records = new ArrayList<ReservationRecord>();
		this.user = user;
	}
	
	public void saveBookRoomRequest(String booking_id, DateReservation date, TimeSlot time_slot)
	{
		records.add(new ReservationRecord(booking_id, date, time_slot));
	}
	
	public boolean canBook()
	{
		return records.size() < 3;
	}
	
	//TODO The question is ambigious
	public void removeOldRecords(DateReservation date, TimeSlot time_slot)
	{
	}
}
