/**
 * 
 */
package comp6231.a1.common;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author saman
 *
 */
public class TimeSlot implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int[] hour;
	private int[] minute;
	private boolean booked;
	
	public TimeSlot(int hour1, int minute1, int hour2, int minute2)
	{
		if (hour1< 0 || hour1 > 23)
			throw new IllegalArgumentException(hour1 + " is invalid for hour");
		if (hour2 < 0 || hour2 > 23)
			throw new IllegalArgumentException(hour2 + " is invalid for hour");
		if (minute1 < 0 || minute1 > 59)
			throw new IllegalArgumentException(minute1 + " is invalid for minute");
		if (minute2 < 0 || minute2 > 59)
			throw new IllegalArgumentException(minute2 + " is invalid for minute");
		if (hour2 < hour1)
			throw new IllegalArgumentException(hour2 + " should be greater than or equal to " + hour1);
		if (hour1 == hour2 && minute2 < minute1)
			throw new IllegalArgumentException(minute2 + " shoulbe ge greater than or equal to " + minute1);
		hour = new int[2];
		hour[0] = hour1;
		hour[1] = hour2;
		minute = new int[2];
		minute[0] = minute1;
		minute[1] = minute2;
		setBooked(false);
	}
	
	public int getTime(int index)
	{
		if (index < 0 || index > 1)
			throw new IllegalArgumentException("index should be between 0 and 1");
		return hour[index] * 60 + minute[index];
	}
	
	@Override
	public String toString()
	{
		return hour[0] + ":" + minute[0] + " - " + hour[1] + ":" + minute[1];
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(hour);
		result = prime * result + Arrays.hashCode(minute);
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
		if (!(obj instanceof TimeSlot))
			return false;
		TimeSlot other = (TimeSlot) obj;
		if (!Arrays.equals(hour, other.hour))
			return false;
		if (!Arrays.equals(minute, other.minute))
			return false;
		return true;
	}

	public boolean conflict(TimeSlot time_interval)
	{
		int[] external_time = new int[2];
		int[] time = new int[2];
		
		for (int i = 0; i < 2; ++i)
		{
			external_time[i] = time_interval.getTime(i);
			time[i] = getTime(i);
		}
		
		if (external_time[0] >= time[0] && external_time[0] <= time[1])
			return true;
		if (external_time[1] >= time[0] && external_time[1] <= time[1])
			return true;
		return false;
	}
	
	int getHour1()
	{
		return hour[0];
	}
	
	int getMinute1()
	{
		return minute[0];
	}
	
	int getHour2()
	{
		return hour[1];
	}
	
	int getMinute2()
	{
		return minute[2];
	}

	public boolean isBooked() {
		return booked;
	}

	public void setBooked(boolean booked) {
		this.booked = booked;
	}	
}
