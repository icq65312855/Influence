package influence.application.controller;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import influence.MainApp;
import influence.application.GraphStage;
import influence.application.model.SimpleTimer;
import influence.application.model.Vertex;
import influence.application.view.GraphView;
import influence.application.view.helpers.GroupInfo;
import influence.application.view.layout.GraphLayout;
import influence.util.GraphLoader;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

/**
 * Controller for influence.application layout. It contains 
 * the event handlers on the form.
 * @author Muidinov Aider
 *
 */
public class ApplicationOverviewController implements Initializable {
	// form elements
	@FXML
	private TableView<GroupInfo> groupTable;
	@FXML
	private TableColumn<GroupInfo,String> leaderColumn;
	@FXML
	private TableColumn<GroupInfo,String> sizeColumn;
	@FXML
	private TextField filenameField;
	@FXML
	private AnchorPane graphPane;
	@FXML
	private Slider influenceSlider;
	@FXML
	private Label influenceLabel;
	@FXML
	private Label percentLeadersLabel;
	@FXML
	private Button showGraphButton;
	
	/** reference to the main influence.application */
	private GraphStage graphStage;
	
	/** main panel on which is located the graph elements */
	private GraphView graphView;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// define the rules for filling in table groups
		leaderColumn.setCellValueFactory(e -> e.getValue().getLeader());
		sizeColumn.setCellValueFactory(e -> e.getValue().getSize());

		// remove all links groups with followers
		// showLinkGroup(null);

		// listening to change the selection, and a change
		// show connect the group with its followers
		groupTable.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> showLinkGroup(newValue));
	}
	
	
	/**
	 * Get access to the main influence.application
	 * @param graphStage main influence.application
	 */
	public void setGraphStage(GraphStage graphStage) {
		// get access
		this.graphStage = graphStage;
		
		// set the current filename
		filenameField.setText(graphStage.getFilename());
		
		// create a basic pane graph
		graphView = new GraphView(graphStage.getGraph());
		graphPane.getChildren().add(graphView);
		
		//set the current value of influence factor
		influenceSlider.setValue(graphStage.getGraph().getReductionInfluence()*100);
		setTextInfluenceLabel(influenceSlider.getValue());
		
		// set start value of opinion leaders percent
		percentLeadersLabel.setText("Waiting for updates...");
		
		// define the possibility to show a graph
		if (graphStage.getGraph().size() == 0) {
			showGraphButton.setDisable(true);
		}
		
		// bind our pane to the main pane
		AnchorPane.setTopAnchor(graphView, 0.0);
		AnchorPane.setLeftAnchor(graphView, 0.0);
		AnchorPane.setRightAnchor(graphView, 0.0);
		AnchorPane.setBottomAnchor(graphView, 0.0);

	}
	
	/**
	 * Show the selected group. Reduce the opacity of elements of the selected group.
	 * @param group selected group
	 */
	private void showLinkGroup(GroupInfo group) {
		if (graphView == null) return;
		
		if (group != null) {
			graphView.selectGroup(group.getLeader().get());
		}
	}
	
	/**
	 * Initialization and drawing the graph.
	 */
	public void showGraph() {
		SimpleTimer timer = new SimpleTimer();
		
		timer.start("Initialising graph...\n");
		graphStage.getGraph().initialize();
		timer.finish("DONE");
		
		groupTable.getItems().clear();
		groupTable.setItems(this.graphStage.getGroupInfoData());
		
		timer.start("Updating graph...\n");
		graphView.update();
		timer.finish("DONE");
		
		GraphLayout graphLayout = new GraphLayout(graphView);
		
		timer.start("Showing graph...\n");
		graphLayout.showGraph();
		timer.finish("DONE");
		
		showOpinionLeaders();
	}
	
	/**
	 * Displays in the console a list of opinion leaders, 
	 * and considers them a percentage of the total number of users
	 */
	private void showOpinionLeaders() {
		int size = graphStage.getGraph().size();
		
		if (size == 0) return;
		
		List<Vertex> opinionLeaders = graphStage.getGraph().getOpinionLeaders(50.0);
		Double percentLeaders = 100.0 * opinionLeaders.size() / graphStage.getGraph().size();
		
		percentLeadersLabel.setText("Total percent of opinion leaders: "+String.format("%(.2f", percentLeaders)+" %");
		
		for (Vertex v : opinionLeaders) System.out.println(v.getId()+" (pop:"+v.getPopularity()+")");
		System.out.println("============== TOTAL ==============");
		System.out.println("Amount of opinion leaders "+opinionLeaders.size()+" out of "+graphStage.getGraph().size()+" users ("+percentLeaders+" %)");
	}
	
	/**
	 * Handler for file selection
	 */
	@FXML
	private void handleChooseFile() {
		FileChooser fileChooser = new FileChooser();
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showOpenDialog(graphStage.getPrimaryStage());
		
		if (file != null) {
			graphStage.setFilename(file.getAbsolutePath());
			filenameField.setText(graphStage.getFilename());
			showGraphButton.setDisable(true);
		}
	}
	
	/**
	 * Handler for manual editing of the file path
	 */
	@FXML
	private void handleEditPath() {
		graphStage.setFilename(filenameField.getText());
		showGraphButton.setDisable(true);
	}
	
	/**
	 * Data load handler. Called when a click on the "Download Data" button.
	 */
	@FXML
	private void handleLoadData() {
		// graph remove before loading data
		graphStage.getGraph().clear();
		
		// download new data
		GraphLoader.loadGraph(graphStage.getGraph(), graphStage.getFilename(), ";");
		
		// if any data has been loaded, it makes available the button "Show graph"
		if (graphStage.getGraph().size() != 0) {
			showGraphButton.setDisable(false);
		}
	}
	
	/**
	 * Handler clicking on the "Show graph" button
	 */
	@FXML
	private void handleShowGraph() {
		if (graphStage.getGraph().size() == 0) {
			System.out.println("Graph is empty!");
			return;
		}
		
		// cancel all previous selection groups
		graphView.setViewSelectedGroup(false);
		
		// show graph
		showGraph();
	}
	
	// simple handler mouse clicks
	@FXML
	private void handleMouseClick(MouseEvent event) {
		if (event.getClickCount() == 2)
        {
			graphView.setViewSelectedGroup(true);
        }
	}
	
	/**
	 * Coefficient selection handler
	 */
	@FXML
	private void slideInfluence() {
		Double influenceReduction = influenceSlider.getValue();
		
		graphStage.getGraph().setReductionInfluence(influenceReduction/100);
		
		setTextInfluenceLabel(influenceReduction);
	}
	
	/**
	 * setting text the coefficient
	 * @param influenceReduction the coefficient value
	 */
	private void  setTextInfluenceLabel(Double influenceReduction) {
		influenceLabel.setText("Force of influence ("+influenceReduction+" %)");
	}
	
	/**
	 * Helper class to handle the double-click. Designed only for handling double-clicking
	 * @author Muidinov Aider
	 *
	 */
	public class SimpleMouseHandler {
		private long firstClick;
		
		public SimpleMouseHandler() {
			this.firstClick = -100;
		}
		
		public boolean isDoubleClick() {
			long secondClick = System.currentTimeMillis();
			long dif = secondClick - firstClick;
			
			if (dif < 200) {
				this.firstClick = -100;
				return true;
			}
			
			this.firstClick = secondClick;
			
			return false;
		}
	}

	
}
