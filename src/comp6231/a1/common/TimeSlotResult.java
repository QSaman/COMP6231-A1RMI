/**
 * 
 */
package comp6231.a1.common;

/**
 * @author saman
 *
 */
public class TimeSlotResult {
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
