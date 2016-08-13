package influence;


import influence.application.GraphStage;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * The main class of the application. Initializes environment variables.
 * It has access to the graph model.
 * @author Muidinov Aider
 *
 */
public class MainApp extends Application {
	
    /**
     * Start the influence.application initializes the root layout and base panel
     */
	@Override
	public void start(Stage primaryStage) {
		GraphStage graphStage = new GraphStage(primaryStage);
		graphStage.setIcon(new Image("file:resources/images/Limewire.png"));
	}

	public static void main(String[] args) {
		launch(args);
	}
}
