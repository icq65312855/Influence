package influence.application;

import java.io.IOException;

import influence.MainApp;
import influence.application.controller.ApplicationOverviewController;
import influence.application.model.FacebookGraph;
import influence.application.view.helpers.GroupInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GraphStage {
	/** access to the primary stage */
	private Stage primaryStage;
	
	/** access to the root layout */
    private BorderPane rootLayout;
    
    /** access to the graph model */
    private FacebookGraph graph;
    
    /** path to a file data */
    private String filename;
    
    /** the data observed in the form of a list of groups. */
    private ObservableList<GroupInfo> groupInfoData = FXCollections.observableArrayList();
	
	
	public GraphStage(Stage stage) {
		
		this.primaryStage = stage;
		this.primaryStage.setTitle("Expansion of influence into fan-pages     © Muidinov Aider");
		
		this.primaryStage.getIcons().add(new Image("file:resources/images/Limewire.png"));
		
		graph = new FacebookGraph();
		
		initRootLayout();
		showApplicationOverview();
		
	}
	
	/**
	 * Initializes the root layout
	 */
	private void initRootLayout() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("application/view/fxml/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();
			
			Scene scene = new Scene(this.rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * It shows at the root of the layout of the graph elements
	 */
	private void showApplicationOverview() {
		try {
			
			// Loading graph form
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("application/view/fxml/ApplicationOverview.fxml"));
			AnchorPane graphReview = (AnchorPane) loader.load();
			
			// Place the graph elements in the center of the root layout.
			rootLayout.setCenter(graphReview);
			
			// Give the controller access to the main influence.application
			ApplicationOverviewController controller = loader.getController();
			controller.setGraphStage(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Get primary stage
	 * @return primary stage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}
	
	/**
	 * Get current filename
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Set new filename
	 * @param filename new filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	/**
	 * Get model graph
	 * @return graph
	 */
	public FacebookGraph getGraph() {
		return graph;
	}

	/**
	 * Get group data from model graph
	 * @return group data
	 */
	public ObservableList<GroupInfo> getGroupInfoData() {
		
		// remove the current data
		groupInfoData.clear();
		
		// It receives data from the model and place them on the form
		groupInfoData.addAll(graph.getGroupsInfo());
		
		return groupInfoData;
	}


}
