package influence.application.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;

import influence.application.model.FacebookGraph;
import influence.application.model.Group;
import influence.application.model.Link;
import influence.application.model.SimpleTimer;
import influence.application.model.Vertex;
import influence.application.view.helpers.EdgeViewFactory;
import influence.application.view.layout.GraphLayout;
import javafx.scene.layout.Pane;

/**
 * Class to work with the graph elements. It adds or removes 
 * the pane elements. It changes the properties of the elements 
 * (for example when select the group change the properties 
 * of some vertices and edges). 
 * It has access to the graph model.
 * @author Muidinov Aider
 *
 */
public class GraphView extends Pane {
	
	/** access to the graph model */
	private FacebookGraph graph;
	
	// sets added elements
	private List<VertexView> vertices;
	private List<EdgeView> edges;
	private HashMap<String,GroupView> groups;
	
	// designed properties that are used in the calculations
	private int maxSize;
	private Double averageRadiusGroup;
	private Double koefSize;
	private Double koefRemote;
	
	// properties to work with selected items
	private GroupView lastSelected;
	private boolean viewSelectedGroup;
	private String selectedGroup;
	
	/**
	 * The main constructor
	 * @param graph the graph model
	 */
	public GraphView(FacebookGraph graph) {
		this.graph = graph;
		this.groups = new HashMap<>();
		this.vertices = new LinkedList<>();
		this.edges = new LinkedList<>();
		this.viewSelectedGroup = false;
	}
	
	/**
	 * Get the graph model
	 * @return graph model
	 */
	public FacebookGraph getGraph() {
		return graph;
	}

	/**
	 * Get the groups of the graph model
	 * @return groups
	 */
	public HashMap<String,GroupView> getGroups() {
		return groups;
	}

	/**
	 * Get the vertices
	 * @return vertices
	 */
	public List<VertexView> getVertices() {
		return vertices;
	}

	/**
	 * Get the edges
	 * @return edges
	 */
	public List<EdgeView> getEdges() {
		return edges;
	}

	/**
	 * Get the maximum size
	 * @return maximum size
	 */
	public int getMaxSize() {
		return maxSize;
	}

	/**
	 * Get the average radius of group
	 * @return average radius of group
	 */
	public Double getAverageRadiusGroup() {
		return averageRadiusGroup;
	}

	/**
	 * get the size factor
	 * @return size factor
	 */
	public Double getKoefSize() {
		return koefSize;
	}

	/**
	 * get the remote factor
	 * @return remote factor
	 */
	public Double getKoefRemote() {
		return koefRemote;
	}

	/**
	 * It determines whether the group display mode is enabled
	 * @return current mode
	 */
	public boolean isViewSelectedGroup() {
		return viewSelectedGroup;
	}

	/**
	 * Sets the view mode for the group
	 * @param viewSelectedGroup new mode
	 */
	public void setViewSelectedGroup(boolean viewSelectedGroup) {
		
		// sets the view mode
		this.viewSelectedGroup = viewSelectedGroup;
		
		if (viewSelectedGroup) {
			
			// update the graph elements
			update();
			
			// positioned the graph elements
			GraphLayout graphLayout = new GraphLayout(this);
			graphLayout.showGraph();
		}
		
	}

	/**
	 * Gets the number of vertices in the graph a given set of groups
	 * @param groupsValues set of groups
	 * @return number of vertices
	 */
	public Double getAmountSizeGroups(Collection<Group> groupsValues) {
		Double amount = 0.0;
		for (Group g : groupsValues) {
			amount += g.getLinks().size();
		}
		
		return amount;
	}
	
	/**
	 * The graph preparation parameters for further processing
	 * @param groupsValues set of groups for which to do preparation parameters
	 */
	public void prepare(Collection<Group> groupsValues) {
		
		// use the priority queue to determine the size of the largest groups
		PriorityQueue<Group> p_group = new PriorityQueue<Group>(groupsValues);
		
		Group majorGroup = p_group.peek();
		int amountGroup = p_group.size();
		
		// If the current resolution is not yet defined, then set it manually
		if (getWidth() == 0 || getHeight() == 0) {
			setWidth(598.0);
			setHeight(598.0);
		}
		
		// get size of the largest groups
		this.maxSize = majorGroup.size();
		
		// get average size of the group based on their total amount
		this.averageRadiusGroup = Math.min(getWidth(), getHeight())/(amountGroup*2.0);
		
		// group size depends on the number members in the group
		this.koefSize = (double) (amountGroup)/getAmountSizeGroups(groupsValues);
		
		// remoteness from the center of the group is dependent on the number of members in the group
		this.koefRemote = 1.00/maxSize;
	}
	
	/**
	 * Adds the elements of the graph on the panel
	 */
	public void update() {
		SimpleTimer timer = new SimpleTimer();
		
		timer.start("   cleaning old data...");
			groups.clear();
			vertices.clear();
			edges.clear();
			getChildren().clear();
		timer.finish("...done");
		
		timer.start("   preparing...");
			Collection<Group> groupsValues;
			
			// if there was a double-click on the group, show only the selected group
			if (viewSelectedGroup) {
				groupsValues = new LinkedList<Group>();
				groupsValues.add(graph.getGroup(graph.getVertex(selectedGroup)));
			} else {
				groupsValues = graph.getGroups().values();
			}
			
			if (groupsValues.isEmpty()) {
				throw new NoSuchElementException("groups is empty");
			}
			
			prepare(groupsValues);
		timer.finish("...done");
		
		timer.start("   updating groups and vertices...");
			// the vertex for connecting edges
			HashMap<String, VertexView> connectionVertex = new HashMap<String, VertexView>();
			
			/* Mode which affects drawing the graph elements. 
			 * If the selected group, the elements of the group should be allocated */
			int mode = viewSelectedGroup ? 1 : 0;
			
			for (Group g : groupsValues) {
				
				GroupView groupView = new GroupView(g, mode);
				groups.put(g.getLeader().getId(),groupView);
				
				for (Link l : g.getLinks()) {
					
					Vertex v = l.getFollower();
					VertexView vertexView = new VertexView(v, groupView, mode);
					vertices.add(vertexView);
					connectionVertex.put(v.getId(), vertexView);
					groupView.getVertices().add(vertexView);
					
				}
			}
		timer.finish("...done");
		
		timer.start("   adding children...");
			getChildren().addAll(groups.values());
			getChildren().addAll(vertices);
		timer.finish("...done");
		
		timer.start("   updating edges...");
			
			/* Create a factory to create edges. Opacity edges will depend on their number, 
			 * to avoid the full pane painting. */
			EdgeViewFactory edgeViewFactory = new EdgeViewFactory(graph.getAmountEdges(connectionVertex.keySet()),mode);
			
			for (Map.Entry<String, HashSet<String>> entry : graph.exportGraph().entrySet()) {
				
				if (connectionVertex.containsKey(entry.getKey())) {
				
					VertexView from = connectionVertex.get(entry.getKey());
					for (String e : entry.getValue()) {
						if (connectionVertex.containsKey(e)) {
							EdgeView edgeView = edgeViewFactory.addEdge(from, connectionVertex.get(e));
							edges.add(edgeView);
							from.getGroupView().getEdgesView().add(edgeView);
						}
					}
					
				}
				
			}
			System.out.print("adding children...");
			getChildren().addAll(edges);
			
		timer.finish("...done");
		
	}
	
	/**
	 * When select a group, select the graph elements
	 * @param nameGroup the name of the selected group
	 */
	public void selectGroup(String nameGroup) {
		
		// set the name of the selected group
		selectedGroup = nameGroup;
		
		// if we are in a mode of viewing the group, then just change the current view groups
		if (viewSelectedGroup) {
			setViewSelectedGroup(viewSelectedGroup);
			return;
		}
		
		// cleanse mark previously selected items
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			selectEdgeView(lastSelected, false);
			selectVertexView(lastSelected,false);
			lastSelected = null;
		}
		
		if (!groups.containsKey(nameGroup)) return;

		// get the latest selected group
		GroupView gv = groups.get(nameGroup);
		gv.setSelected(!gv.isSelected());
		lastSelected = gv;
		
		// select vertices and edges that belong to the selected group
		selectEdgeView(gv,true);
		selectVertexView(gv,true);
	}
	
	/**
	 * Changing selection of edges
	 * @param gv group
	 * @param flag new flag
	 */
	private void selectEdgeView(GroupView gv, boolean flag) {
		for (EdgeView ev : gv.getEdgesView()) {
			ev.setSelected(flag);
		}
	}
	
	/**
	 * Changing selection of vertices
	 * @param gv group
	 * @param flag new flag
	 */
	private void selectVertexView(GroupView gv, boolean flag) {
		for (VertexView vv : gv.getVertices()) {
			vv.setSelected(flag);
		}
	}
}
