package console;
import java.io.IOException;
import java.io.OutputStream;

import console.InputStreamHandler.MyRunnable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import edu.kit.joana.wala.core.Main;

public class DialogConsoleEventHandler {
	
	private Process process;
	private Thread inputThread;
	private Thread heartbeatThread;
	
	@FXML
	private TextArea taOutput;
	@FXML
	private TextField tfInput;
	@FXML
	private Button btnClose;

	public static DialogConsoleEventHandler showDialog(Window owner) {
		DialogConsoleEventHandler _return =null;
		try {
			FXMLLoader loader = new FXMLLoader(Main.class.getResource("/console.fxml"));
			Parent root = loader.load();
			_return = loader.getController();
			Scene scene = new Scene(root);
			Stage stage = new Stage();
			stage.initOwner(owner);
			stage.setTitle("Running application");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return _return;
	}
	
	@FXML
	protected void close(ActionEvent event){
		Stage s = (Stage)btnClose.getScene().getWindow();
		s.close();
		this.process.destroy();
		inputThread.stop();
		heartbeatThread.interrupt();
	}
	
	@FXML
	protected void handleInputOk(KeyEvent event){
		if(event.getCode().equals(KeyCode.ENTER)){
			try {
				taOutput.setText(taOutput.getText()+System.getProperty("line.separator")+tfInput.getText());
				String text = tfInput.getText()+System.getProperty("line.separator");
				this.process.getOutputStream().write(text.getBytes());
				this.process.getOutputStream().flush();
				tfInput.setText("");
				if(!this.process.isAlive()){
					this.close(null);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}
		}
	}
	
	public TextArea getTaOutput(){
		return this.taOutput;
	}
	public TextField getTfInput(){
		return this.tfInput;
	}
	
	public void start(Process p){
		this.process = p;
		InputStreamHandler ish = new InputStreamHandler(this, process.getInputStream());
		this.inputThread = new Thread(ish);
		this.inputThread.start();
		
		this.heartbeatThread = new Thread(){
			public void run(){
				while(!isInterrupted()){
					if(!process.isAlive()){
						Platform.runLater(new Runnable(){
							public void run(){
								close(null);	
							}
						});
						interrupt();
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
//						e.printStackTrace();
					}
				}
			}
		};
		this.heartbeatThread.start();
	}
	
	

}
