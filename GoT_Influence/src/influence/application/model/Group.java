package influence.application.model;

import java.util.HashSet;

/**
 * It represents a group of followers of the leader. 
 * Stores the leader and all links with this group, 
 * as well as the rank of the group (the sum of all 
 * the forces of links to this group)
 * @author Muidinov Aider
 *
 */
public class Group implements Comparable<Group>{
	private Vertex leader;
	private Double rank;//equal to the sum of the ranks links
	private HashSet<Link> links;

	/**
	 * The main constructor of group. Creates a new group for leader
	 * @param owner
	 */
	public Group(Vertex owner) {
		this.leader = owner;
		this.rank = 0.00;
		this.links = new HashSet<Link>();
	}

	/**
	 * Get leader of the group
	 * @return leader of the group
	 */
	public Vertex getLeader() {
		return leader;
	}

	/**
	 * Get rank of the group
	 * @return rank of the group
	 */
	public Double getRank() {
		return rank;
	}
	
	/**
	 * Get all links for this group.
	 * @return links of the group
	 */
	public HashSet<Link> getLinks() {
		return links;
	}
	
	/** Gets the user with the highest rank */
	public Vertex getMajor() {
		Vertex major = null;
		Double maxRank = 0.0;
		
		for (Link l : links) {
			Double currRank = l.getFollower().getRank();
			
			if (currRank > maxRank) {
				maxRank = currRank;
				major = l.getFollower();
			}
		}
		
		return major;
	}

	/**
	 * Adding a new link into the group
	 * @param link adding link
	 */
	public void addLink(Link link) {
		this.links.add(link);
		this.rank += link.getRank();
	}
	
	/**
	 * Deleting link from the group
	 * @param link deleting link
	 */
	public void deleteLink(Link link) {
		if (this.links.contains(link)) {
			this.links.remove(link);
			this.rank -= link.getRank();
		}
	}
	
	/**
	 * Get amount of links in the group
	 * @return amount of links
	 */
	public int size() {
		return this.links.size();
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Group) {
			Group g = (Group) (obj);
			Vertex v = g.getLeader();
			return v.getId().equals(this.leader.getId());
		}
		
		return false;
	}
	
	/**
	 * Display detail info of the group
	 * @param theGraph this graph
	 * @return string with describe of the group
	 */
	public String infoGroup(FacebookGraph theGraph) {
		String info = this.toString()+"\n";
		info += "   EGO "+theGraph.getEgonet(this.leader)+"\n";
		info += "   INC "+theGraph.getFollowers(this.leader);
		
		return info;
	}
	
	public String toString() {
		String s = this.leader+" (total: "+this.links.size()+")\t"+this.links;
		
		return s;
	}
	
	@Override
	public int compareTo(Group o) {
		if (this.links.size() < o.getLinks().size()) {
			return 1;
		} else if (this.links.size() > o.getLinks().size()) {
			return -1;
		}

		return 0;
	}
	
}
