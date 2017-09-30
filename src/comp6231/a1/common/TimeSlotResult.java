/**
 * 
 */
package comp6231.a1.common;

import java.io.Serializable;

/**
 * @author saman
 *
 */
public class TimeSlotResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String campusName;
	private int totalAvailableSlots;
	
	public TimeSlotResult(String campus_name, int total_available_slots) {
		campusName = campus_name;
		totalAvailableSlots = total_available_slots;
	}
	
	public String getCampusName() {
		return campusName;
	}
	public int getTotalAvailableSlots() {
		return totalAvailableSlots;
	}

}
