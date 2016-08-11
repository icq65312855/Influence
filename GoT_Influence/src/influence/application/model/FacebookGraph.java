/**
 * 
 */
package influence.application.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

import influence.application.model.Edge;
import influence.application.model.Vertex;
import influence.application.view.helpers.GroupInfo;

/**
 * The main class to represent the graph. 
 * Allows access to the vertices of the graph, 
 * the outgoing edges for each vertex and the edges incoming to each vertex.
 * I left the implementation Graph interface to retain 
 * the possibility of realization of graphs for different data
 *
 * @author Aider Muidinov.
 */
public class FacebookGraph implements Graph {
	private HashMap<String, Vertex> vertices;
	
	/** outgoing edges for each vertex */
	private HashMap<String, HashSet<Edge>> edges;
	
	/** incoming edges for each vertex */
	private HashMap<String, HashSet<Edge>> incomingEdges;
	
	/** groups in the graph. As a Map key is set id group leader */
	private HashMap<Vertex,Group> groups;
	
	/** an indication that the graph has been initialized */
	private boolean initialized;
	
	/** groups search was held */
	private boolean searched;
	
	/** total number of edges */
	private Integer amountEdges;
	
	/** It determines the degree of reducing the influence of the group 
	 * on the indirect followers. For large values, the stronger group 
	 * will absorb the weak and vice versa. */
	private double reductionInfluence;
	
	/**
	 * The default constructor initializes all fields initial values
	 */
	public FacebookGraph() {
		this.vertices = new HashMap<String, Vertex>();
		this.edges = new HashMap<String, HashSet<Edge>>();
		this.incomingEdges = new HashMap<String, HashSet<Edge>>();
		this.groups = new HashMap<Vertex,Group>();
		this.initialized = false;
		this.searched = false;
		this.amountEdges = 0;
		this.reductionInfluence = 0.5;
	}

	/**
	 * Gets the value for reductionInfluence
	 * @return reductionInfluence
	 */
	public double getReductionInfluence() {
		return reductionInfluence;
	}

	/**
	 * Sets the value for reductionInfluence
	 * @param reductionInfluence
	 */
	public void setReductionInfluence(double reductionInfluence) {
		this.reductionInfluence = reductionInfluence;
		resetSearched();
	}

	/**
	 * Gets the value for groups
	 * @return groups
	 */
	public HashMap<Vertex,Group> getGroups() {
		return groups;
	}

	/**
	 * Gets amount edges
	 * @return amountEdges
	 */
	public Integer getAmountEdges() {
		return amountEdges;
	}

	/**
	 * Gets the value for initialized
	 * @return initialized
	 */ 
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * resets the initialization indication
	 */
	public void resetInitialized() {
		this.initialized = false;
		resetSearched();
	}

	/**
	 * Gets the value for searched
	 * @return searched
	 */
	public boolean isSearched() {
		return searched;
	}

	/**
	 * resets the searched indication
	 */
	public void resetSearched() {
		this.searched = false;
	}

	/** Converting hashset of vertices in the linked list */
	public List<Vertex> getVertices(Collection<Vertex> v) {
		PriorityQueue<Vertex> p_vertices = new PriorityQueue<Vertex>(new Comparator<Vertex>() {

			@Override
			public int compare(Vertex o1, Vertex o2) {
				return o1.getId().compareTo(o2.getId());
			}
			
		});
		p_vertices.addAll(v);
		List<Vertex> l_vertices = new LinkedList<Vertex>();
		
		while (!p_vertices.isEmpty()) l_vertices.add(p_vertices.remove());
		
		return l_vertices;
	}
	
	/** Converting hashset of vertices in the linked list */
	public List<Vertex> getVertices() {
		return getVertices(vertices.values());
	}
	
	/**
	 * Converting hashset of edges in the linked list
	 * @param vertexID vertex ID
	 * @return linked list of edges
	 */
	public List<Edge> getEdges(String vertexID) {
		if (!edges.containsKey(vertexID)) return new LinkedList<Edge>();
		
		PriorityQueue<Edge> p_edges = new PriorityQueue<Edge>(edges.get(vertexID));
		List<Edge> l_edges = new LinkedList<Edge>();
		
		while (!p_edges.isEmpty()) l_edges.add(p_edges.remove());
		
		return l_edges;
	}
	
	/**
	 * Converting hashset of incoming edges in the linked list
	 * @param vertexID vertex ID
	 * @return linked list of incoming edges
	 */
	public List<Edge> getIncomingEdges(String vertexID) {
		if (!incomingEdges.containsKey(vertexID)) return new LinkedList<Edge>();
		
		PriorityQueue<Edge> p_edges = new PriorityQueue<Edge>(incomingEdges.get(vertexID));
		List<Edge> l_edges = new LinkedList<Edge>();
		
		while (!p_edges.isEmpty()) l_edges.add(p_edges.remove());
		
		return l_edges;
	}

	/*
	 * (non-Javadoc)
	 * @see influence.application.model.Graph#initialise()
	 */
	public void initialize() {
		SimpleTimer timer = new SimpleTimer();
		
		if (!initialized) {
			timer.start("   creation of new edges...");
			createEdges();
			timer.finish("...done");

			timer.start("   merging of edges...");
			mergeEdges();
			timer.finish("...done");

			timer.start("   define ranks...");
			defineRanks();
			timer.finish("...done");
			
			initialized = true;
		}

		if (!searched) {
			timer.start("   clear old groups...");
			clearGroups();
			timer.finish("...done");
			timer.start("   search groups...");
			searchGroups();
			timer.finish("...done");
			
			searched = true;
		}
	}
	
	/**
	 * Calculation of full rank for all vertices
	 */
	private void defineRanks() {
		// defines the basic ranks of users
		for (Vertex user : getVertices()) {
			user.defineBaseRank(getIncomingEdges(user.getId()));
		}
		
		// defines the full rank of users
		for (Vertex user : getVertices()) {
			user.defineRank(getIncomingEdges(user.getId()));
		}
	}
	
	/**
	 * Search groups and delete empty groups
	 */
	private void searchGroups() {
		// We prepare a set of all vertices for search for groups
		HashSet<Vertex> hashModified = new HashSet<Vertex>(this.vertices.values());
		
		// basic search groups
		defineGroups(hashModified);
		
		/* re-search for merge groups in which only one members.
		 * This must be done, because there are situations where 
		 * all members of the group moved on to other groups */
		defineGroups(resetSingleGroup());
		
		// delete empty groups, because they do not carry useful information
		removeEmptyGroups();
	}
	
	/**
	 * Determine the set of vertices with one member
	 * @return groups in which only one members
	 */
	private HashSet<Vertex> resetSingleGroup() {
		HashSet<Vertex> hashModified = new HashSet<Vertex>();
		
		for (Group g : this.groups.values()) {
			if (g.size() == 1) {
				for (Link l : g.getLinks()) {
					if (!hashModified.contains(l.getFollower())) {
						l.resetRank();
						hashModified.add(l.getFollower());
						if (g.getLeader().getLink().getGroup().getLeader().equals(l.getFollower()) 
								&& g.getLeader().getLink().getGroup().size() == 1)
							hashModified.add(g.getLeader());
					}
				}
			}
		}
		
		return hashModified;
	}
	
	/**
	 * Defines a group of users.
	 * Search is only for a given set of vertices.
	 * @param hashModified
	 */
	private void defineGroups(HashSet<Vertex> hashModified) {
		// create queue of vertices
		LinkedList<Vertex> modified = (LinkedList<Vertex>) getVertices(hashModified);
		// we look through the vertex in our queue on a first in - first out
		while (!modified.isEmpty()) {
			Vertex user = modified.removeFirst();
			hashModified.remove(user);
			
			/* In this condition, we first find the connection with the group 
			 * or create one, if the group has not yet been created. 
			 * Then define the strongest group of adherents in our personal network. */
			if (user.findLink(this).changeGroup(this)) {
				
				/* if the current group has been changed, it will add to the queue 
				 * all users to incoming edges, because perhaps it could change their group */
				for (Edge e : getFollowers(user)) {
					if (!hashModified.contains(e.getBegin())) {
						modified.addLast(e.getBegin());
						hashModified.add(e.getBegin());
					}
				}
				
			}
			
		}
	}
	
	/**
	 * Remove empty groups, because they no longer need us.
	 */
	private void removeEmptyGroups() {
		Set<Vertex> removeGroups = new HashSet<Vertex>();
		
		for (Map.Entry<Vertex, Group> entry : this.groups.entrySet()) {
			int size = entry.getValue().getLinks().size();
			if (size == 0) {
				removeGroups.add(entry.getKey());
			}
		}
		
		for (Vertex g : removeGroups) {
			this.groups.remove(g);
		}
	}
	
	/**
	 * Creating edges that are not loaded and are auxiliary for organizing influence spread
	 */
	public void createEdges() {
		List<Edge> newEdges = new LinkedList<Edge>();
		
		for (Map.Entry<String, HashSet<Edge>> v : edges.entrySet()) {
			for (Edge e : v.getValue()) {
				if (e.getType() == Type.COMMENT) {
					newEdges.add(new Edge(e.getEnd(), e.getBegin(),"FEEDBACK"));
				} else if (e.getType() == Type.LIKE) {
					newEdges.add(new Edge(e.getEnd(), e.getBegin(),"GRATITUDE"));
				}
			}
		}
		
		// adding new edges
		this.addEdges(newEdges);
	}
	
	/**
	 * Merges edge if they have the same orientation. 
	 * Calculated edge rank based on their quantity. 
	 * Thus, more edges was merged in one direction, 
	 * the higher the rank of edge.
	 */
	public void mergeEdges() {
		List<Edge> newEdges = new LinkedList<Edge>();
		//create merge edges
		for (Vertex v : getVertices()) {
			Map<Vertex,Edge> searched = new HashMap<Vertex,Edge>();
			for (Edge e : getEdges(v.getId())) {
				Vertex end = e.getEnd();
				if (searched.containsKey(end)) {
					Edge merge = searched.get(end);
					merge.mergeEdge(e);
				} else {
					Edge merge = new Edge(e,"GENERAL");
					searched.put(end, merge);
					newEdges.add(merge);
				}
			}
		}
		//delete old edges
		this.edges.clear();
		this.incomingEdges.clear();
		amountEdges = 0;
		//adding new edges
		this.addEdges(newEdges);
	}
	
	/* (non-Javadoc)
	 * @see influence.application.model.graph.Graph#addVertex(int)
	 */
	@Override
	public void addVertex(String num) {
		if (!vertices.containsKey(num)) {
			Vertex vertex = new Vertex(num);
			vertices.put(num, vertex);
		}
		
		if (initialized) resetInitialized();
	}

	/**
	 * Adds an edge in sets of edges. Edge will not be added 
	 * if it is missing the vertex a set of vertices. 
	 * Resets indication graph initialization.
	 * @param edge added edge
	 */
	public void addEdge(Edge edge) {
		String from = edge.getBegin().getId();
		String to = edge.getEnd().getId();
		
		if (!this.vertices.containsKey(from)) return;
		if (!this.vertices.containsKey(to)) return;
		
		HashSet<Edge> currEdges;
		
		//outgoing edges
		if (!this.edges.containsKey(from)) {
			currEdges = new HashSet<Edge>();
			this.edges.put(from, currEdges);
		} else {
			currEdges = this.edges.get(from);
		}
		currEdges.add(edge);
		
		//incoming edges
		if (!this.incomingEdges.containsKey(to)) {
			currEdges = new HashSet<Edge>();
			this.incomingEdges.put(to, currEdges);
		} else {
			currEdges = this.incomingEdges.get(to);
		}
		currEdges.add(edge);
		
		amountEdges++;
		
		if (initialized) resetInitialized();
	}
	
	/* (non-Javadoc)
	 * @see influence.application.model.graph.Graph#addEdge(String, String)
	 */
	@Override
	public void addEdge(String from, String to, String type) {
		if (from.isEmpty() || to.isEmpty()) return;
		if (!this.vertices.containsKey(from)) return;
		if (!this.vertices.containsKey(to)) return;
		
		Edge edge = new Edge(this.vertices.get(from),this.vertices.get(to),type);
		addEdge(edge);
	}
	
	/**
	 * the addition of a set of edges
	 * @param newEdges set of edges
	 */
	public void addEdges(List<Edge> newEdges) {
		// adding new edges
		for (Edge e : newEdges) {
			this.addEdge(e);
		}
	}
	
	/**
	 * creating and adding the group to the set of a graph of groups
	 * @param v vertex to create a group
	 * @return created group
	 */
	private Group addGroup(Vertex v) {
		Group newGroup = new Group(v);
		this.groups.put(v, newGroup);
		
		return newGroup;
	}
	
	/**
	 * remove the edge from all sets of edges
	 * @param edge removable edge
	 */
	public void deleteEdge(Edge edge) {
		String from = edge.getBegin().getId();
		String to = edge.getEnd().getId();
		
		HashSet<Edge> currEdges = new HashSet<Edge>();
		
		// outgoing edges
		if (this.edges.containsKey(from)) {
			currEdges = this.edges.get(from);
		}
		currEdges.remove(edge);

		// incoming edges
		if (this.incomingEdges.containsKey(to)) {
			currEdges = this.incomingEdges.get(to);
		}
		currEdges.remove(edge);
		
		amountEdges--;
	}
	
	/**
	 * Delete Group. To do this, remove the 
	 * connection with this group for each node 
	 * and remove the group from the set of a graph of groups
	 * @param delGroup Group to remove
	 */
	public void deleteGroup(Group delGroup) {
		for (Link l : delGroup.getLinks()) {
			l.getFollower().removeLink();
		}
		groups.remove(delGroup);
	}
	
	/**
	 * Deleting all sets of vertices, edges and groups. 
	 * Dropping flags for initialization and search.
	 */
	public void clear() {
		vertices.clear();
		edges.clear();
		incomingEdges.clear();
		groups.clear();
		resetInitialized();
		resetSearched();
		amountEdges = 0;
	}
	
	/**
	 * To delete all groups.
	 */
	public void clearGroups() {
		for (Group g : groups.values()) {
			deleteGroup(g);
		}
		groups.clear();
		resetSearched();
	}
	
	/** Adds new edges. 
	 * New vertices will be created if they do not exist. 
	 */
	public void newEdge(String from, String to, String type) {
		if (from.isEmpty() || to.isEmpty()) return;
		if (!this.vertices.containsKey(from)) this.addVertex(from);
		if (!this.vertices.containsKey(to)) this.addVertex(to);
		
		this.addEdge(from,to,type);
	}
	
	/**
	 * Getting to the edges of the personal network. 
	 * If there is no personal network it returns an empty set.
	 * @param v the vertices of which are looking for the personal network
	 * @return set of edges constituting personal network
	 */
	public List<Edge> getEgonet(Vertex v) {
		if (this.edges.containsKey(v.getId())) {
			return getEdges(v.getId());
		} else {
			return new LinkedList<Edge>();
		}
	}
	
	/**
	 * Getting followers for a given node. Are all incoming edges.
	 * @param v the top of which are looking for the followers
	 * @return set of vertices constituting followers
	 */
	public List<Edge> getFollowers(Vertex v) {
		if (this.incomingEdges.containsKey(v.getId())) {
			return getIncomingEdges(v.getId());
		} else {
			return new LinkedList<Edge>();
		}
	}

	/**
	 * Gets the group for the group leader
	 * @param v group leader
	 * @return group for the group leader
	 */
	public Group getGroup(Vertex v) {
		if (this.groups.containsKey(v)) {
			return this.groups.get(v);
		}
		
		return this.addGroup(v);
	}
	
	/**
	 * Gets vertex on its Id
	 * @param v id of vertex
	 * @return vertex
	 */
	public Vertex getVertex(String v) {
		return vertices.get(v);
	}
	
	/**
	 * Finds the vertices with the highest rank value.
	 * List each time is calculated as it would be 
	 * necessary to maintain the relevance of these values
	 * @param percent
	 * @return list of Vertex with opinion leaders
	 */
	public List<Vertex> getOpinionLeaders(double percent) {
		List<Vertex> opinionLeaders = new ArrayList<Vertex>();
		PriorityQueue<Vertex> users = new PriorityQueue<Vertex>(new Comparator<Vertex>() {

			@Override
			public int compare(Vertex o1, Vertex o2) {
				if (o1.getPopularity() < o2.getPopularity()) {
					return 1;
				} else if (o1.getPopularity() > o2.getPopularity()) {
					return -1;
				}
				return 0;
			}
			
		});
		
		Double totalPopularity = 0.00;
		for (Vertex user : vertices.values()) {
			if (user.getPopularity() > 0.00) {
				users.add(user);
				totalPopularity += user.getPopularity();
			}
		}
		
		Double amountPopularity = percent*totalPopularity/100;
		
		while (amountPopularity >= 0 && !users.isEmpty()) {
			Vertex user = users.remove();
			amountPopularity -= user.getPopularity();
			opinionLeaders.add(user);
		}
		return opinionLeaders;
	}
	
	/**
	 * Get number of edges for a given set of id vertices...
	 * @param ce ids set of vertices
	 * @return amount of edges
	 */
	public Integer getAmountEdges(Collection<String> ce) {
		Integer amount = 0;
		
		for (String entry : ce) {
			if (edges.containsKey(entry)) {
				for (Edge edge : edges.get(entry)) {
					if (edge.getPointsPopularity() > 0) {
						amount++;
					}
				}
			}
		}
		
		return amount;
	}

	/* (non-Javadoc)
	 * @see influence.application.model.Graph#exportGraph()
	 */
	@Override
	public HashMap<String, HashSet<String>> exportGraph(){
		HashMap<String, HashSet<String>> theGraph = new HashMap<String, HashSet<String>>();
		for (Map.Entry<String, Vertex> entry : vertices.entrySet()) {
			HashSet<String> currEdges = new HashSet<String>();
			theGraph.put(entry.getKey(), currEdges);
			if (edges.containsKey(entry.getKey())) {
				for (Edge edge : edges.get(entry.getKey())) {
					if (edge.getPointsPopularity() > 0) {
						currEdges.add(edge.getEnd().getId());
					}
				}
			}
		}
		
		return theGraph;
	}

	/**
	 * Gets information about groups graph. Contains the ID leader and group size
	 * @return list of groups
	 */
	public List<GroupInfo> getGroupsInfo() {
		List<GroupInfo> groupsInfo = new ArrayList<GroupInfo>();
		PriorityQueue<Group> p_group = new PriorityQueue<Group>(groups.values());
		
		while (!p_group.isEmpty()) {
			Group g = p_group.remove();
			groupsInfo.add(new GroupInfo(g.getLeader().getId(), String.valueOf(g.size())));
		}
		
		return groupsInfo;
	}
	
	/* (non-Javadoc)
	 * @see influence.application.model.graph.Graph#printGraph()
	 */
	@Override
	public void printGraph() {
		for (Vertex v : getVertices()) {
			System.out.print("["+v.getId()+"] (rank:"+v.getRank()+"->(");
			String coma = "";
			if (edges.containsKey(v.getId())) {
				for (Edge edge : getEdges(v.getId())) {
					System.out.print(coma + edge.getEnd().getId());
					coma = ",";
				}
			}
			System.out.println(")");
		}
		
	}
	
	/**
	 * Displays detailed information on the graph.
	 * it only makes sense on small data
	 */
	public void printStatistics() {
		System.out.println("==========TOTAL=============");
		System.out.println("Vertices: "+this.size());
		System.out.println("Edges: "+this.getAmountEdges());
		System.out.println("Groups: "+this.groups.size());
	}
	
	/* (non-Javadoc)
	 * @see influence.application.model.graph.Graph#size()
	 */
	@Override
	public int size() {
		return this.vertices.size();
	}

}
