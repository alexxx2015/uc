package de.tum.in.i22.uc.gui;

import java.util.Iterator;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class UcMangerFX extends Application {
	private GuiController controller;

	public static void main(String[] args) {
		GuiController controller = new GuiController();
		launch(args);
	}

	@Override
	public void init() {
		this.controller = new GuiController();
		this.controller.setApplication(this);		
		SceneGenerator.controller = this.controller;
	}

	@Override
	public void start(Stage stage) throws Exception {
		SceneGenerator.stage = stage;
		stage.setTitle("Usage Control Cockpit");

		BorderPane bp = new BorderPane();

		bp.setTop(this.controller.getSceneGenerator().generateTop());

		bp.setCenter(this.controller.getSceneGenerator().generateCenter());
		Scene s = new Scene(bp, 500, 500);
		s.getStylesheets().add("css/mystyle.css");
		stage.setScene(s);
		
		stage.show();
	}

}
