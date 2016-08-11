/**
 * @author UCSD MOOC development team
 * 
 * Utility class to add vertices and edges to a graph
 *
 */
package influence.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import influence.application.model.Graph;
import influence.application.model.SimpleTimer;

public class GraphLoader {
    /**
     * Loads graph with data from a file.
     * The file should consist of lines with 3 strings each, corresponding
     * to a "from" vertex and a "to" vertex. And type of edges.
     * @throws IOException 
     */ 
    public static void loadGraph(Graph g, String filename, String delimiter) {
        SimpleTimer timer = new SimpleTimer();
    	Set<String> seen = new HashSet<String>();
        FileReader myFile = null;
        BufferedReader buff = null;
        
        timer.start("   loading '"+filename+"'...");
        
        try {
            myFile = new FileReader(filename);
            buff = new BufferedReader(myFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // Iterate over the lines in the file, adding new
        // vertices as they are found and connecting them with edges.
        try {
			while (buff.ready()) {
				String vv = buff.readLine();
				String[] v = vv.split(delimiter);
				if (v.length != 3) {
					continue;
				}
				String v1 = v[0];
				String v2 = v[1];
				String v3 = v[2];
				if (v1.equals(v2)) {
					continue;
				}
			    if (!seen.contains(v1)) {
			        g.addVertex(v1);
			        seen.add(v1);
			    }
			    if (!seen.contains(v2)) {
			        g.addVertex(v2);
			        seen.add(v2);
			    }
			    g.addEdge(v1, v2, v3);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				buff.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
        
        timer.finish("...done");
    }
    
    public static void loadGraph(Graph g, String filename) throws IOException {
    	loadGraph(g, filename, " ");
    }
}
