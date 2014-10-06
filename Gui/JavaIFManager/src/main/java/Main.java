import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
	
	static protected JavaPipCommunicationManager jPipCommunicationManager;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primStage) throws Exception {
		// TODO Auto-generated method stub
		Parent root = FXMLLoader.load(getClass().getResource("/main.fxml"));
		Scene scene = new Scene(root);

		primStage.setTitle("Java Static Information Flow Analyzer");
		primStage.setScene(scene);
		primStage.sizeToScene();
		primStage.show();
	}	
}
