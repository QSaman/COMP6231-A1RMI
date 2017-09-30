/**
 * 
 */
package comp6231.a1.common;

import java.io.Serializable;

/**
 * @author saman
 *
 */
public class DateReservation implements Comparable<DateReservation>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int year;
	private int month;
	private int day;
	
	public DateReservation(String date) {
		String[] tokens = date.split("-");
		if (tokens.length != 3)
			throw new IllegalArgumentException("Invalid date string");
		int[] tmp = new int[3];
		for (int i = 0; i < 3; ++i)
			tmp[i] = Integer.parseInt(tokens[i].trim());
		setDate(tmp[2], tmp[1], tmp[0]);
	}
	
	public DateReservation(int year, int month, int day)
	{
		setDate(year, month, day);
	}

	@Override
	public int compareTo(DateReservation right) {
		int delta = year - right.year;
		if (delta != 0)
			return delta;
		delta = month - right.month;
		if (delta != 0)
			return delta;
		delta = day - right.day;
		return delta;
	}
	
	private void setDate(int year, int month, int day)
	{
		if (year < 1)
			throw new IllegalArgumentException("Invalid year " + year);
		if (month < 1 || month > 12)
			throw new IllegalArgumentException("Invalid month " + month);
		if (day < 1 || day > 31)
			throw new IllegalArgumentException("Invalid day " + day);
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	@Override
	public String toString()
	{
		return "year: " + year + ", month: " + month + ", day: " + day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}	
}
