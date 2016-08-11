package influence.application.view.helpers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Helper class for displaying a list of groups.
 * @author Muidinov Aider
 *
 */
public class GroupInfo {
	private final StringProperty leader;
	private final StringProperty size;
	
	/**
	 * The main constructor of the class
	 * @param leader leader of group
	 * @param size size of group
	 */
	public GroupInfo(String leader, String size) {
		this.leader = new SimpleStringProperty(leader);
		this.size = new SimpleStringProperty(size);
		
	}

	/**
	 * Get leader of group
	 * @return leader of group
	 */
	public StringProperty getLeader() {
		return leader;
	}

	/**
	 * Get size of group
	 * @return size of group
	 */
	public StringProperty getSize() {
		return size;
	}
	
}
