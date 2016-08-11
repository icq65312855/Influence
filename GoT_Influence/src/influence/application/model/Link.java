package influence.application.model;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * It represents a user connection to the group. 
 * Stores group, followers and the power of communication. 
 * Each user is defined by the only one link with the group.
 * @author Muidinov Aider
 *
 */
public class Link implements Comparable<Link> {
	private Group group;
	private Vertex follower;
	private Double rank;
	
	/**
	 * The main communication constructor. Adds a link to the 
	 * specified successor. Defines the its group or create 
	 * a new group as the base (based only on direct links)
	 * @param follower new follower group
	 * @param theGraph this graph
	 */
	public Link(Vertex follower, FacebookGraph theGraph) {
		this.follower = follower;
		defineGroup(theGraph);
		this.group.addLink(this);
	}
	
	/**
	 * getting rank of link
	 * @return rank of link
	 */
	public Double getRank() {
		return rank;
	}

	/**
	 * getting group of link
	 * @return group of link
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * getting follower of group
	 * @return follower of group
	 */
	public Vertex getFollower() {
		return follower;
	}
	
	/**
	 * reset of rank
	 */
	public void resetRank() {
		this.rank = 0.00;
	}

	/**
	 * It defines a core group to create a connection. 
	 * Select from vertex the personal network, which has 
	 * the greatest rank and for which the highest rank of edge. 
	 * Taken into account the product of these parameters.
	 * @param theGraph this graph
	 */
	private void defineGroup(FacebookGraph theGraph) {
		Double maxRank = 0.00;
		Vertex leader = this.follower;
		
		for (Edge e : theGraph.getEgonet(this.follower)) {
			Double currRank = e.getRank()*e.getEnd().getRank();
			if (currRank > maxRank) {
				leader = e.getEnd();
				maxRank = currRank;
			}
		}
		this.group = theGraph.getGroup(leader);
		this.rank = maxRank;
		this.group.addLink(this);
	}
	
	/**
	 * Defines the a new group for the vertex by selecting it from 
	 * the followers of the personal network. The more 
	 * the adherents of one group, the more likely that 
	 * the group will be selected. The new connection will be equal to 
	 * the rank of middle-ranking adherents links and will be reduced 
	 * by a factor of influence.
	 * @param theGraph this graph
	 * @return indication was to change the group or not
	 */
	public boolean changeGroup(FacebookGraph theGraph) {
		// get a group of user egonet
		HashMap<Group, Adherents> groups = new HashMap<Group, Adherents>();
		Adherents adherents = new Adherents(this.group,this.getRank());
		groups.put(this.group, adherents);
		Adherents mainAdherents = adherents;

		for (Edge v : theGraph.getEgonet(follower)) {
			Link link = v.getEnd().findLink(theGraph);
			Group currGroup = link.getGroup();

			if (groups.containsKey(currGroup)) {
				adherents = groups.get(currGroup);
				adherents.addRank(link.getRank() * theGraph.getReductionInfluence());
			} else {
				adherents = new Adherents(link.getGroup(),link.getRank() * theGraph.getReductionInfluence());
				groups.put(currGroup, adherents);
			}
		}

		// find the adherents with the highest rank
		Double maxRank = 0.00;
		PriorityQueue<Adherents> p_adherents = new PriorityQueue<Adherents>(groups.values());

		while (!p_adherents.isEmpty()) {
			Adherents a = p_adherents.remove();
			Double currRank = a.getFullRank();
			if (currRank > maxRank) {
				maxRank = currRank;
				mainAdherents = a;
			}
		}

		// Assign users to a group only if it differs from the current of his
		// group.
		if (!this.group.equals(mainAdherents.getGroup())) {
			this.group.deleteLink(this);
			this.group = mainAdherents.getGroup();
			this.rank = mainAdherents.getRank();
			this.group.addLink(this);
			
			return true;
		}
		
		return false;
	}
	
	public String toString() {
//		return this.group.getLeader().getId()+"<--"+this.follower.getId()+" <rl "+this.rank+">";
		return this.follower.toString();
	}
	
	@Override
	public int compareTo(Link o) {
		if (this.getRank() < o.getRank()) {
			return 1;
		} else if (this.getRank() > o.getRank()) {
			return -1;
		}

		return 0;
	}
}
