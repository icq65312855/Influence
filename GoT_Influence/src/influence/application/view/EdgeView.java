package influence.application.view;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

/**
 * Presentation of edges to view. Displays a line connecting the vertices.
 * @author Muidinov Aider
 *
 */
public class EdgeView extends Line {
	private VertexView from;
	private VertexView to;
	private boolean selected;
	private Color colorEdge;
	
	/**
	 * The main constructor of the class
	 * @param from starting vertex
	 * @param to finishing vertex
	 * @param color edge color
	 */
	public EdgeView(VertexView from, VertexView to, Color color) {
		this.from = from;
		this.to = to;
		this.selected = false;
		this.colorEdge = color;
		
		// set the color of an edge
		setStroke(color);
		
		// bind to the vertices of edge
        startXProperty().bind( from.layoutXProperty());
        startYProperty().bind( from.layoutYProperty());
        endXProperty().bind( to.layoutXProperty());
        endYProperty().bind( to.layoutYProperty());
        
        // tooltips (very slow)
//        Tooltip tooltip = new Tooltip("from "+from.getVertex().getId()+" (pop: "+from.getVertex().getPopularity()+")"+
//        		" --> "+to.getVertex().getId()+ " (pop: "+to.getVertex().getPopularity()+")");
//		Tooltip.install(this, tooltip);
        
	}

	/**
	 * Gets the start vertex
	 * @return start vertex
	 */
	public VertexView getFrom() {
		return from;
	}

	/**
	 * Gets the finish vertex
	 * @return finish vertex
	 */
	public VertexView getTo() {
		return to;
	}

	/**
	 * Gets the selected properties
	 * @return selected properties
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Set the selected properties
	 * @param selected new selected properties
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if (selected) {
			// make the selected group more opaque
			Color c = from.getGroupView().getColorGroup();
			setStroke(new Color(c.getRed(),c.getGreen(),c.getBlue(),colorEdge.getOpacity()));
		} else {
			setStroke(colorEdge);
		}
	}
}
