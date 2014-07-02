package de.tum.in.i22.uc.gui;

import java.io.File;
import java.util.List;
import java.util.Map;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class SceneGenerator {
	protected static Stage stage;
	protected static GuiController controller;
	
	protected Button btn_start;
	protected Button btn_stop;
	
	protected Button btn_deployPolicy;
	protected Button btn_refreshPip;
	
	protected TabPane tabpane_center;
	

	public Node generateTop() {
		HBox hb = new HBox();
		hb.setSpacing(5.0);
		hb.setPadding(new Insets(10, 10, 10, 10));
		this.btn_start = new Button("Start Usage Control");
		this.btn_start.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				controller.startUc();
			}
		});
		hb.getChildren().add(btn_start);

		this.btn_stop = new Button("Stop Usage Control");
		this.btn_stop.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				controller.stopUc();
			}
			
		});
		hb.getChildren().add(btn_stop);
		return hb;
	}

	public Node generateCenter() {
		this.tabpane_center = new TabPane();

		Tab pdpTab = new Tab();
		pdpTab.setClosable(false);
		pdpTab.setText("PDP");
		pdpTab.setContent(generatePdpTab());

		Tab pipTab = new Tab();
		pipTab.setClosable(false);
		pipTab.setText("PIP");
		pipTab.setContent(generatePipTab());

		this.tabpane_center.getTabs().addAll(pdpTab, pipTab);
		return this.tabpane_center;
	}

	private Node generatePipTab() {
		AnchorPane anchorpane = new AnchorPane();

		this.btn_refreshPip = new Button("Refresh");
		AnchorPane.setTopAnchor(this.btn_refreshPip, 10.0);
		AnchorPane.setLeftAnchor(this.btn_refreshPip, 10.0);

		TextArea ta = new TextArea();
		AnchorPane.setTopAnchor(ta, 35.0);
		AnchorPane.setLeftAnchor(ta, 10.0);
		AnchorPane.setBottomAnchor(ta, 10.0);
		AnchorPane.setRightAnchor(ta, 10.0);

		anchorpane.getChildren().addAll(this.btn_refreshPip, ta);

		return anchorpane;
	}

	private Node generatePdpTab() {
		AnchorPane anchorPane = new AnchorPane();
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(5);
		this.btn_deployPolicy = new Button("Deploy Policy");
		this.btn_deployPolicy.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub

				FileChooser fc = new FileChooser();
				fc.setTitle("Choose Policy");
				fc.setInitialDirectory(new File("/home/alex/Policies"));
				File f = fc.showOpenDialog(SceneGenerator.stage);
				controller.deployMechanisms(f);
			}
		});
		vbox.getChildren().add(this.btn_deployPolicy);
		
		Map<String, List<String>> deployedMech = controller.getDeployedMechanisms();
		
		TableView<Map<String,List<String>>> tv = new TableView<Map<String,List<String>>>();
		TableColumn<Map<String,List<String>>,String> policyName = new TableColumn<Map<String,List<String>>,String>("Policy Name");
		
		TableColumn<Map<String,List<String>>,String> mechanismName = new TableColumn<Map<String,List<String>>,String>("Mechanism Name");
		tv.getColumns().add(policyName);
		tv.getColumns().add(mechanismName);
		vbox.getChildren().add(tv);

		return vbox;
	}

	
	protected void switchGuiCmp(boolean p){
		this.btn_deployPolicy.setDisable(!p);
		this.btn_refreshPip.setDisable(!p);		
		this.btn_stop.setDisable(!p);
		this.tabpane_center.setDisable(!p);
		this.btn_start.setDisable(p);
	}
}
