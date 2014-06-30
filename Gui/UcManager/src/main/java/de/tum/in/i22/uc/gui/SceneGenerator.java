package de.tum.in.i22.uc.gui;

import java.io.File;

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
	

	public Node generateTop() {
		HBox hb = new HBox();
		hb.setSpacing(5.0);
		hb.setPadding(new Insets(10, 10, 10, 10));
		btn_start = new Button("Start Usage Control");
		btn_start.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub
				controller.startUc();
			}
		});
		hb.getChildren().add(btn_start);

		btn_stop = new Button("Stop Usage Control");
		btn_stop.setDisable(true);
		hb.getChildren().add(btn_stop);
		return hb;
	}

	public Node generateCenter() {
		TabPane tabPane = new TabPane();

		Tab pdpTab = new Tab();
		pdpTab.setClosable(false);
		pdpTab.setText("PDP");
		pdpTab.setContent(generatePdpTab());

		Tab pipTab = new Tab();
		pipTab.setClosable(false);
		pipTab.setText("PIP");
		pipTab.setContent(generatePipTab());

		tabPane.getTabs().addAll(pdpTab, pipTab);
		return tabPane;
	}

	private Node generatePipTab() {
		AnchorPane anchorpane = new AnchorPane();

		Button btn = new Button("Refresh");
		AnchorPane.setTopAnchor(btn, 10.0);
		AnchorPane.setLeftAnchor(btn, 10.0);

		TextArea ta = new TextArea();
		AnchorPane.setTopAnchor(ta, 35.0);
		AnchorPane.setLeftAnchor(ta, 10.0);
		AnchorPane.setBottomAnchor(ta, 10.0);
		AnchorPane.setRightAnchor(ta, 10.0);

		anchorpane.getChildren().addAll(btn, ta);

		return anchorpane;
	}

	private static Node generatePdpTab() {
		AnchorPane anchorPane = new AnchorPane();
		VBox vbox = new VBox();
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(5);
		Button btn = new Button("Deploy Policy");
		btn.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event arg0) {
				// TODO Auto-generated method stub

				FileChooser fc = new FileChooser();
				fc.setTitle("Choose Policy");
				File f = fc.showOpenDialog(SceneGenerator.stage);
			}
		});
		vbox.getChildren().add(btn);

		TableView tv = new TableView();
		tv.setPrefHeight(100);
		TableColumn policyName = new TableColumn("Policy Name");
		TableColumn mechanismName = new TableColumn("Mechanism Name");
		tv.getColumns().add(policyName);
		tv.getColumns().add(mechanismName);
		vbox.getChildren().add(tv);

		return vbox;
	}
}
