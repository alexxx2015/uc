import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;


public class DialogEventHandler{
	private Stage stage;
	
	@FXML
	private Label labMessage;
	
	@FXML
	private Label labDetails;
	
	@FXML
	private Button okButton;
	
	@FXML
	private Button cancelButton;
	
	public void initialize(){
		labMessage.setText("ALERT");
		labDetails.setText("Select a program first!");
	}
	
	public void showDialog(Window owner){
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/alert.fxml"));			
			Scene scene = new Scene(root);
			this.stage = new Stage();
			this.stage.initOwner(owner);
			this.stage.initModality(Modality.WINDOW_MODAL);
			this.stage.setScene(scene);
			this.stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@FXML
	protected void okPressed(){
		Stage s = (Stage)okButton.getScene().getWindow();
		s.close();
	}
	
	@FXML
	protected void cancelPressed(){
		Stage s = (Stage)cancelButton.getScene().getWindow();
		s.close();
	}

}
