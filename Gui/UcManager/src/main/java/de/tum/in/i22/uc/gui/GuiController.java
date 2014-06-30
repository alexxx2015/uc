package de.tum.in.i22.uc.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javafx.application.Application;
import javafx.scene.control.Button;

import javax.swing.table.DefaultTableModel;

import de.tum.in.i22.uc.Controller;

public class GuiController extends Controller {

	private Application application;
	private boolean running;
	private SceneGenerator sceneGenerator;

	public GuiController() {
		// TODO Auto-generated constructor stub
		this.sceneGenerator = new SceneGenerator();
	}

	public SceneGenerator getSceneGenerator() {
		return sceneGenerator;
	}

	public Application getApplication() {
		return application;
	}

	protected void setApplication(Application application) {
		this.application = application;
	}

	public boolean isRunning() {
		return running;
	}

	protected void setRunning(boolean running) {
		this.running = running;
	}

	protected void startUc() {

		if (!this.isRunning()) {
			this.setRunning(true);

			this.sceneGenerator.btn_start.setDisable(true);
			this.sceneGenerator.btn_stop.setDisable(false);
			if (!isStarted()) {
				start();
			}
		}
	}

}
