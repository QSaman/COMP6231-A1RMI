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
					
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campusName == null) ? 0 : campusName.hashCode());
		result = prime * result + totalAvailableSlots;
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
		if (!(obj instanceof TimeSlotResult))
			return false;
		TimeSlotResult other = (TimeSlotResult) obj;
		if (campusName == null) {
			if (other.campusName != null)
				return false;
		} else if (!campusName.equals(other.campusName))
			return false;
		if (totalAvailableSlots != other.totalAvailableSlots)
			return false;
		return true;
	}

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
