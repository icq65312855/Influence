package influence.application.view;

import influence.application.model.Vertex;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Presentation of vertex to view. Displays a circle of small diameter.
 * @author Muidinov Aider
 *
 */
public class VertexView extends Circle {
	
	/** access to the main pane the graph */
	private GroupView groupView;
	
	/** model vertex */
	private Vertex vertex;
	
	/** the vertex the selection mark */
	private boolean selected;
	
	/** the vertex the display mode */
	private int mode;
	
	/**
	 * The main constructor the summit. When you create a the vertex 
	 * is defined by its radius, and color.
	 * @param vertex model of the vertex
	 * @param groupView pane of the graph
	 * @param mode display mode
	 */
	public VertexView(Vertex vertex, GroupView groupView, int mode) {
		this.vertex = vertex;
		this.groupView = groupView;
		this.mode = mode;
		setId(vertex.getId());
		
		// for leaders of groups larger radius
		if (groupView.getGroup().getLeader().equals(vertex)) {
			setRadius(3.00);
		} else {
			setRadius(1.50);
		}
		setFill(new Color(0.0,0.0,0.0,0.4));
		
		// mode display vertices for groups only increases their opacity and radius
		if (mode == 1) {
			setFill(new Color(0.0,0.0,0.0,0.8));
			setRadius(getRadius()*2.00);
			// tooltips (very slow)
			Tooltip tooltip = new Tooltip(vertex.getId()+ " (popularity: "+vertex.getPopularity()+")");
			Tooltip.install(this, tooltip);
		}
		
	}

	/**
	 * get model of vertex
	 * @return vertex
	 */
	public Vertex getVertex() {
		return vertex;
	}

	/**
	 * get pane of the graph
	 * @return groupView
	 */
	public GroupView getGroupView() {
		return groupView;
	}

	/**
	 * Get display mode
	 * @return mode
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Get mark of selected
	 * @return selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set the mark selection vertex
	 * @param selected new mark
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if (selected) {
			Color c = groupView.getColorGroup();
			setFill(new Color(c.getRed(),c.getGreen(),c.getBlue(),0.9));
		} else {
			setFill(new Color(0.0,0.0,0.0,0.15));
		}
	}
	
}
