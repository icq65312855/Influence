package influence.application.view.helpers;

import influence.application.view.EdgeView;
import influence.application.view.VertexView;
import javafx.scene.paint.Color;

/**
 * Helper class to create edges. Determines the opacity and 
 * color of edges based on the total number of edges
 * @author Muidinov Aider
 *
 */
public class EdgeViewFactory {
	
	/** the total number of edges */
	private Integer amount;
	
	/** edges display mode */
	private int mode;
	
	/**
	 * The main class constructor
	 * @param amount the total number of edges
	 * @param mode edges display mode
	 */
	public EdgeViewFactory(int amount, int mode) {
		this.amount  = amount;
		this.mode = mode;
	}
	
	/**
	 * Add an edge between two vertices
	 * @param from Start vertex
	 * @param to Finishing vertex
	 * @return edge
	 */
	public EdgeView addEdge(VertexView from, VertexView to) {
		Color edgeColor = Color.gray(0.8, 0.1);
		
		// The more edges, the lower opacity
		Double opacity = (amount == 0 ? 1.0 : 200.0/amount);
		opacity = Math.min(1.0, opacity);
				
		if (mode == 0) {
			edgeColor = Color.gray(0.01, opacity);
		} else if (mode == 1) {
			edgeColor = Color.gray(0.01, opacity);
		}
		
		return new EdgeView(from, to, edgeColor);
	}
}
