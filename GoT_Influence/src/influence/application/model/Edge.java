package influence.application.model;

/** list of types of links */
enum Type {
	LIKE,
	COMMENT,
	FEEDBACK,
	GRATITUDE,
	GENERAL;
}

/**
 * Represents connection between vertices. 
 * Each edge is Like or comment per user to another. 
 * Each edge has a rank that is calculated when creating. 
 * The edge can be downloaded or created.
 * @author maestro
 *
 */
public class Edge implements Comparable<Edge> {
	
	/** Reduction factor of influence of each subsequent Like or Comment */
	public static final double REDUCTION_RATIO = 0.9;
	
	/** starting vertex of edge */
	private Vertex begin;
	
	/** finishing vertex of edge */
	private Vertex end;
	
	/** edge type (like, comment, ...) */
	private Type type;
	
	/** edge rank to calculate the influence */
	private Double rank;
	
	/** popularity points, which gives the Like or Comment */
	private Double pointsPopularity;
	
	/** properties edge used in calculating the grade */
	private Double koefRank;

	/**
	 * The main constructor edge. Creates an edge on the string 
	 * representation of vertices
	 * @param begin starting vertex ID
	 * @param end finishing vertex ID
	 */
	public Edge(Vertex begin, Vertex end) {
		this(begin,end,"GENERAL");
	}
	
	/**
	 * Support edge constructor. Create an edge by copying another edge, and a new type.
	 * @param e The edge to be copied
	 * @param nameType The new name of type
	 */
	public Edge(Edge e, String nameType) {
		this.begin = e.getBegin();
		this.end = e.getEnd();
		this.type = Edge.getFromString(nameType);
		this.rank = e.getRank();
		this.pointsPopularity = e.getPointsPopularity();
		this.koefRank = e.getKoefRank();
	}
	
	/**
	 * Support edge constructor. It creates a new edge using 
	 * the values of vertices and a new type.
	 * @param begin starting vertex
	 * @param end finishing vertex
	 * @param nameType name of new type
	 */
	public Edge(Vertex begin, Vertex end, String nameType) {
		this.begin = begin;
		this.end = end;
		this.type = Edge.getFromString(nameType);
		this.koefRank = 1.00;
		defineRankOptions();
	}
	
	/**
	 * It determines the type of edge on the type name. 
	 * To type the name of an unknown type will be returned LIKE.
	 * @param value The string representation of the type
	 * @return The resulting type
	 */
	public static Type getFromString(String value) {
		try {
		switch (value) {
			case "User Liked Comment": return Type.LIKE;
			case "User Liked Post": return Type.LIKE;
			case "User Commented Comment": return Type.COMMENT;
			case "User Commented Post": return Type.COMMENT;
			default: return Type.valueOf(value); 
		}
		} catch(IllegalArgumentException e) {
			System.out.println("(!) Unknown type of edge: <"+value+">");
			return Type.LIKE;
		}
	}

	/**
	 * Get the starting edge vertex
	 * @return starting edge vertex
	 */
	public Vertex getBegin() {
		return begin;
	}

	/**
	 * Get the finishing edge vertex
	 * @return finishing edge vertex
	 */
	public Vertex getEnd() {
		return end;
	}
	
	/**
	 * Get the rank of edge
	 * @return rank of edge
	 */
	public Double getRank() {
		return rank;
	}

	/**
	 * Get the points popularity
	 * @return points popularity
	 */
	public Double getPointsPopularity() {
		return pointsPopularity;
	}

	/**
	 * Get the type of edge
	 * @return type of edge
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Get the coefficient of rank
	 * @return coefficient of rank
	 */
	public Double getKoefRank() {
		return koefRank;
	}

	/**
	 * Defines the edge parameters based on the type of edge.
	 * Sets the initial value of the rank and rank step.
	 */
	private void defineRankOptions() {
		if (this.type == Type.LIKE) {
			this.rank = 1.00;
			this.pointsPopularity = 1.0;
		} else if (this.type == Type.COMMENT) {
			this.rank = 2.00;
			this.pointsPopularity = 2.0;
		} else if (this.type == Type.FEEDBACK) {
			this.rank = 1.00;
			this.pointsPopularity = 0.0;
		} else if (this.type == Type.GRATITUDE) {
			this.rank = 0.5;
			this.pointsPopularity = 0.0;
		} else {
			this.rank = 0.00;
			this.pointsPopularity = 0.0;
		}
	}
	
	/**
	 * Merges one edge to the other. When the merger increases the rank of the resulting edge.
	 * If at least one edge is real, the resulting edge is real.
	 * @param e the other edge
	 */
	public void mergeEdge(Edge e) {
		if (e.type == Type.LIKE) {
			// reduce the coefficient
			this.koefRank *= Edge.REDUCTION_RATIO;
			// reduce the rank of current edge
			e.rank *= this.koefRank;
			// reduce the popularity or current edge
			e.pointsPopularity *= this.koefRank;
		} else if (e.type == Type.COMMENT) {
			// reduce only the coefficient
			this.koefRank *= Edge.REDUCTION_RATIO;
		} else if (e.type == Type.FEEDBACK || e.type == Type.GRATITUDE) {
			// reduce only the rank of edge
			e.rank *= this.koefRank;
		}
		
		this.rank += e.rank;
		this.pointsPopularity += e.pointsPopularity;
	}
	
	/**
	 * Changes the direction of the edge (swaps the edges).
	 */
	public void transpose() {
		Vertex temp = this.begin;
		this.begin = end;
		this.end = temp;
	}
	
	public String toString() {
		return ""+this.begin.getId()+"->"+this.end.getId()+" re <"+this.rank+">";
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Edge) {
			Edge e = (Edge)(obj);
			return e.getEnd().equals(this.end) && e.getBegin().equals(this.begin);
		}
		
		return obj.equals(this);
	}
	
	// Code to implement Comparable
	@Override
	public int compareTo(Edge o) {
		Edge e = (Edge) o;
		String str1 = this.getBegin().getId()+this.getEnd().getId()+this.getType();
		String str2 = e.getBegin().getId()+e.getEnd().getId()+e.getType();

		return str1.compareTo(str2);
	}
}
