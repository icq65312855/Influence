package influence.application.view;

import java.util.HashSet;

import influence.application.model.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Presentation of groups to view. It displays a circle 
 * of a certain diameter that matches the size of the group. 
 * Color of circle also depends on the number of participants in the group.
 * @author Muidinov Aider
 *
 */
public class GroupView extends Pane {
	
	// group settings to show
	private Group group;
	private Circle view;
	private Double koefRemoteVertex;
	private Double centerXCurrentGroup;
	private Double centerYCurrentGroup;
	private Double radiusGroup;
	private Color colorGroup;
	
	// sets of vertices and edges belonging to the group
	private HashSet<VertexView> vertices;
	private HashSet<EdgeView> edgesView;
	
	// group display mode
	private int mode; 
	
	// group selection marker
	private boolean selected;
	
	/**
	 * The main constructor the group
	 * @param group group model
	 * @param mode group display mode
	 */
	public GroupView(Group group, int mode) {
		this.group = group;
		this.selected = false;
		this.vertices = new HashSet<VertexView>();
		this.edgesView = new HashSet<EdgeView>();
		this.mode = mode;
	}
	
	/**
	 * Setting group display options
	 * @param radius radius of the group
	 * @param color color of the group
	 */
	public void setView(Double radius, Color color) {
		if (mode == 1) color = new Color(0,0,0,0);
		view = new Circle(radius, radius, radius, color);
		setWidth(radius*2.0);
		setHeight(radius*2.0);
		
		getChildren().add(view);
		
//		Tooltip tooltip = new Tooltip("Leader: "+group.getLeader().getId()+" (pop: "+group.getLeader().getPopularity()+")\n"
//									+"Major: "+group.getMajor().getId()+" (pop: "+group.getMajor().getPopularity()+")");
//		Tooltip.install(view, tooltip);
		
		this.radiusGroup = radius;
		this.colorGroup = color;
	}

	/**
	 * Gets the value for group
	 * @return group
	 */
	public Group getGroup() {
		return group;
	}

	/**
	 * Gets the value for koefRemoteVertex
	 * @return koefRemoteVertex
	 */
	public Double getKoefRemoteVertex() {
		return koefRemoteVertex;
	}

	/**
	 * Sets the value for koefRemoteVertex
	 * @param koefRemoteVertex new coefficient
	 */
	public void setKoefRemoteVertex(Double koefRemoteVertex) {
		this.koefRemoteVertex = koefRemoteVertex;
	}

	/**
	 * Gets the center of the current group
	 * @return X coordinate
	 */
	public Double getCenterXCurrentGroup() {
		return centerXCurrentGroup;
	}
	
	/**
	 * Gets the center of the current group
	 * @return Y coordinate
	 */
	public Double getCenterYCurrentGroup() {
		return centerYCurrentGroup;
	}

	/**
	 * Sets the center of the current group
	 * @param centerXCurrentGroup X coordinate
	 */
	public void setCenterXCurrentGroup(Double centerXCurrentGroup) {
		this.centerXCurrentGroup = centerXCurrentGroup;
	}

	/**
	 * Sets the center of the current group
	 * @param centerYCurrentGroup Y coordinate
	 */
	public void setCenterYCurrentGroup(Double centerYCurrentGroup) {
		this.centerYCurrentGroup = centerYCurrentGroup;
	}

	/**
	 * Gets the group radius
	 * @return radius
	 */
	public Double getRadiusGroup() {
		return radiusGroup;
	}

	/**
	 * Gets color of the group
	 * @return color
	 */ 
	public Color getColorGroup() {
		return colorGroup;
	}

	/**
	 * Sets color of the group
	 * @param colorGroup new color
	 */
	public void setColorGroup(Color colorGroup) {
		this.colorGroup = colorGroup;
	}

	/**
	 * Gets vertices of the group
	 * @return vertices
	 */
	public HashSet<VertexView> getVertices() {
		return vertices;
	}

	/**
	 * Gets the value for edgesView
	 * @return edgesView
	 */
	public HashSet<EdgeView> getEdgesView() {
		return edgesView;
	}

	/**
	 * Gets the value for mode
	 * @return mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Gets the value for selected
	 * @return selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set the mark selection group
	 * @param selected new mark
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		getChildren().remove(view);
		
		// increase the opacity of the selected group
		if (selected && mode == 0) {
			Color selectedColor = new Color(colorGroup.getRed(), colorGroup.getGreen(), colorGroup.getBlue(), 0.5);
			view = new Circle(this.radiusGroup, this.radiusGroup, this.radiusGroup, selectedColor);
		} else {
			view = new Circle(this.radiusGroup, this.radiusGroup, this.radiusGroup, colorGroup);
		}
		
		getChildren().add(view);
	}
}
