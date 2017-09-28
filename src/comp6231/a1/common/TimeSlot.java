/**
 * 
 */
package comp6231.a1.common;

/**
 * @author saman
 *
 */
public class TimeSlot {
	private int[] hour;
	private int[] minute;
	
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
	}
	
	public int getTime(int index)
	{
		if (index < 0 || index > 1)
			throw new IllegalArgumentException("index should be between 0 and 1");
		return hour[index] * 60 + minute[index];
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

}
