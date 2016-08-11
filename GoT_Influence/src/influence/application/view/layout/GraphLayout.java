package influence.application.view.layout;

import java.util.Random;

import influence.application.model.Group;
import influence.application.model.Link;
import influence.application.model.SimpleTimer;
import influence.application.model.Vertex;
import influence.application.view.GraphView;
import influence.application.view.GroupView;
import influence.application.view.VertexView;
import javafx.scene.paint.Color;

/**
 * The graph layout for positioning the graph elements
 * @author Muidinov Aider
 *
 */
public class GraphLayout {
	
	/** access to the pane the graph */
	private GraphView graph;
	Random rnd;
	
	/**
	 * The main constructor of the class. It provides access to the pane the graph.
	 * @param graph the pane the graph
	 */
	public GraphLayout(GraphView graph) {
		this.graph = graph;
		this.rnd = new Random();
	}
	
	public void showGraph() {
		SimpleTimer sTimer = new SimpleTimer();
		// show groups
		sTimer.start("   groups processing...");
		
		// get position center pane
		Double centerX = graph.getWidth()/2.0;
		Double centerY = graph.getHeight()/2.0;
		
		for (GroupView groupView : graph.getGroups().values()) {
			Group currentGroup = groupView.getGroup();
			int currentSize = currentGroup.size();

			/* Get a random position on the circle. The radius of the circle depends 
			 * on the coefficient of remoteness from the center of the group.*/
			Double[] newLocate = randomPosition(centerX, centerY,
					(1 - currentSize * graph.getKoefRemote()) * Math.min(centerX, centerY));
			
			Double centerXGroup = newLocate[0];
			Double centerYGroup = newLocate[1];
			
			// The radius of this group depends on the number of members the group
			Double radiusGroup = graph.getAverageRadiusGroup() * currentSize * graph.getKoefSize();
			
			/* Color group will change depending on the number members in the group. 
			 * The largest group is of red color, the smallest group will be blue. */
			Color colorGroup = getColorGroup(currentSize, graph.getMaxSize());
			
			// Inscribe this group in the pane
			centerXGroup = Math.max(centerXGroup, radiusGroup);
			centerYGroup = Math.max(centerYGroup, radiusGroup);
			centerXGroup = Math.min(centerXGroup, graph.getWidth() - radiusGroup);
			centerYGroup = Math.min(centerYGroup, graph.getHeight() - radiusGroup);
			
			// Set the color and size of the group
			groupView.setView(radiusGroup, colorGroup);
			
			// Set the the position of the group
			groupView.relocate(centerXGroup-radiusGroup, centerYGroup-radiusGroup);
			
			// Set the parameters of the group needed to position the vertices
			groupView.setKoefRemoteVertex(1/getMaxRank(currentGroup));
			groupView.setCenterXCurrentGroup(groupView.getWidth()/2.0);
			groupView.setCenterYCurrentGroup(groupView.getHeight()/2.0);
		}
		sTimer.finish("   ...done");
			
		// show vertices	
		sTimer.start("   vertices processing...");
		for (VertexView vertexView : graph.getVertices()) {
			Vertex v = vertexView.getVertex();
			GroupView groupView = vertexView.getGroupView();
			
			Double currentRank = v.getRank();

			/* Get a random position on the circle. The radius of the circle depends 
			 * on the coefficient of remoteness from the center of the vertex.*/
			Double[] newLocate = randomPosition(groupView.getCenterXCurrentGroup(),
					groupView.getCenterYCurrentGroup(), (1-currentRank * groupView.getKoefRemoteVertex()) * groupView.getRadiusGroup());
			
			// for group leaders and followers of different radius of groups
			Double radius = vertexView.getRadius();
			
			// // Set the the position of the vertex
			vertexView.relocate(newLocate[0] + groupView.getLayoutX()-radius, 
								newLocate[1] + groupView.getLayoutY()-radius);
		}
		sTimer.finish("   ...done");
		
	}
	
	/**
	 * Gets the maximum rank of group member
	 * @param g group
	 * @return maximum rank
	 */
	private Double getMaxRank(Group g) {
		Double maxRank = 0.0;
		
		for (Link l : g.getLinks()) {
			Double rank = l.getFollower().getRank();
			if (rank > maxRank) {
				maxRank = rank;
			}
		}
		
		return maxRank;
	}
	
	/**
	 * Gets random position on the circle using a random angle deviation
	 * @param x X-axis position of center
	 * @param y Y-axis position of center
	 * @param r radius of the circle
	 * @return random coordinates on the circle
	 */
	private Double[] randomPosition(Double x, Double y, Double r) {
		Double[] point = new Double[2];
		
		Double x0 = x;
		Double y0 = y - r;
		
		Double rx = x0 - x;
		Double ry = y0 - y;
		
		Double alpha = (2 * Math.PI) * rnd.nextDouble();
		
		Double c = Math.cos(alpha);
		Double s = Math.sin(alpha);
		point[0] = x + rx * c - ry * s;
		point[1] = y + rx * s + ry * c;
		
		return point;
	}
	
	/**
	 * Get a random position on the circle. The radius of the circle depends 
	 * on the coefficient of remoteness from the center of the vertex.
	 * @param size the current size of the group
	 * @param maxSize maximum size of groups
	 * @return color of the group
	 */
	private Color getColorGroup(int size, int maxSize) {
		Double blue = 1.00 - ((double) (size)/maxSize);
		Double green = (double) (size)/((double) (maxSize)/2);
		if (green > 1.00) {
			green = 2.00 - green;
		}
		Double red = ((double) (size)/maxSize);
		
		return new Color(red, green, blue, 0.2);
	}
}
