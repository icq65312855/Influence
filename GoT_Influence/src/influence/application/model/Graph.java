package influence.application.model;

import java.util.HashMap;
import java.util.HashSet;

/**
 * The interface implements a basic class methods 
 * needed to work with the graph. Reserved for possible 
 * future compatibility.
 * @author Muidinov Aider
 *
 */
public interface Graph {
    /* Creates a groupView with the given number. */
    public void addVertex(String num);
    
    /* Creates an edge from the first groupView to the second. */
    public void addEdge(String from, String to, String type);
    
    /* Print the influence.application.model.graph */
    public void printGraph();
    
    /* Return amount vertices in the influence.application.model.graph */
    public int size();
    
    /* Initializes the calculated parameters */
    public void initialize();
    
    /* Return the graph's connections in a readable format. 
     * The keys in this HashMap are the vertices in the graph.
     * The values are the nodes that are reachable via a directed
     * edge from the corresponding key. 
	 * The returned representation ignores edge weights and 
	 * multi-edges.  */
    public HashMap<String, HashSet<String>> exportGraph();

} 
