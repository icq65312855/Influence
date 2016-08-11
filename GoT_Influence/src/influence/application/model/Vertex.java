package influence.application.model;

import java.util.List;

/**
 * Represents the vertex of the graph. Stores ID, grades 
 * and communicate with a group of the vertex. 
 * Each vertex is a user.
 * @author Muidinov Aider
 */
public class Vertex implements Comparable<Vertex> {
	
	/**	user rank coefficient influence on the increase in the ranks of other users. */
	public static final double RANK_FACTOR = 0.1;
	
	private String id;
	/** basic rank which is calculated without taking into account the followers of rank */
	private Double baseRank;
	/** full rank which is calculated taking into account the rank of followers */
	private Double rank;
	/** basic popularity which is calculated without taking into account the followers of popularity */
	private Double basePopularity;
	/** popularity which is calculated taking into account the popularity of followers */
	private Double popularity;
	
	/** link users with group */
	private Link link;
	
	/** the default constructor */
	public Vertex(String id) {
		this.id = id;
		this.baseRank = 0.00;
		this.rank = 0.00;
		this.popularity = 0.0;
		this.basePopularity = 0.0;
		this.link = null;
	}

	/**
	 * gets value of ID
	 * @return ID
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * gets value of baseRank
	 * @return baseRank
	 */
	public double getBaseRank() {
		return baseRank;
	}
	
	/**
	 * gets value of rank
	 * @return rank
	 */
	public double getRank() {
		return rank;
	}
	
	/**
	 * gets value of basePopularity
	 * @return basePopularity
	 */
	public double getBasePopularity() {
		return basePopularity;
	}

	/**
	 * gets value of popularity
	 * @return popularity
	 */
	public double getPopularity() {
		return popularity;
	}

	/**
	 * gets value of link
	 * @return link
	 */
	public Link getLink() {
		return link;
	}

	/**
	 * Gets user connection to the group. It creates a new link is not specified
	 * @param graph Current schedule. It is necessary to create a new link with the group.
	 * @return link with group
	 */
	public Link findLink(FacebookGraph graph) {
		if (this.link == null) {
			this.link = new Link(this,graph);
		}
		
		return link;
	}
	
	/**
	 * Deleting connection with the group
	 */
	public void removeLink() {
		this.link = null;
	}

	/**
	 * Calculates the base rank for vertex
	 * @param edges incoming edges
	 */
	public void defineBaseRank(List<Edge> edges) {
		Double r = 0.00;
		Double p = 0.00;
		
		if (edges != null) {
			for (Edge e : edges) {
				r += e.getRank();
				p += e.getPointsPopularity();
			}
		}

		this.baseRank = r;
		this.basePopularity = p;
	}
	
	/**
	 * Calculates the full rank for vertex
	 * @param edges incoming edges
	 */
	public void defineRank(List<Edge> edges) {
		Double fullRank = 0.00;
		Double fullPopularity = 0.00;
		
		if (edges != null) {
			for (Edge e : edges) {
				fullRank += e.getRank() * (1 + e.getBegin().getBaseRank() * Vertex.RANK_FACTOR / 100.00);
				fullPopularity += e.getPointsPopularity() * (1 + e.getBegin().getBasePopularity() * Vertex.RANK_FACTOR / 100.00);
			}
		}

		this.rank = fullRank;
		this.popularity = fullPopularity;
	}
	
	public String toString() {
		return id+"<rv "+this.rank+">";
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Vertex) {
			Vertex v = (Vertex)(obj);
			return v.getId().equals(this.id);
		}
		
		return false;
	}
	
	// Code to implement Comparable
	@Override
	public int compareTo(Vertex o) {
		Vertex v = (Vertex) o;

		if (this.getRank() < v.getRank()) {
			return 1;
		} else if (this.getRank() > v.getRank()) {
			return -1;
		}

		return 0;
	}
}
