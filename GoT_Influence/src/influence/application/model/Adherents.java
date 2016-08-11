package influence.application.model;

/**
 * Additional class used for the calculation of 
 * the strongest groups in the user's personal network.
 * @author Muidinov Aider
 *
 */
public class Adherents implements Comparable<Adherents>{
	
	/** group of adherents */
	private Group group;
	
	/** total rank links all adherents */
	private Double fullRank;
	
	/** amount of adherents */
	private Integer amount;
	
	/**
	 * The main constructor
	 * @param group group of adherent
	 * @param rank Rank initial connection. It may differ from the rank of adherent connection.
	 */
	public Adherents(Group group, Double rank) {
		this.group = group;
		this.fullRank = rank;
		this.amount = 1;
	}

	/**
	 * Getting group
	 * @return group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Getting full rank
	 * @return full rank
	 */
	public Double getFullRank() {
		return fullRank;
	}

	/**
	 * Getting the number of adherents. It is necessary to calculate the average rank.
	 * @return group the number of links
	 */
	public Integer getAmount() {
		return amount;
	}
	
	/**
	 * Getting average rank
	 * @return average rank
	 */
	public Double getRank() {
		if (this.amount != 0) {
			return fullRank/amount;
		}
		
		return 0.00;
	}
	 /**
	  * Adding the rank of new adherent
	  * @param rank add rank
	  */
	public void addRank(Double rank) {
		this.fullRank += rank;
		this.amount++;
	}
	
	// Code to implement Comparable
	@Override
	public int compareTo(Adherents o) {
		Adherents a = (Adherents) o;

		String str1 = this.getGroup().getLeader().getId();
		String str2 = a.getGroup().getLeader().getId();
		
		return str1.compareTo(str2);
	}

}
