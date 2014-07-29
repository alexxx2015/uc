package de.tum.in.i22.uc.gui;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class GuiController extends Controller {

	private Application application;
	private boolean running;
	private SceneGenerator sceneGenerator;

	private final ThriftClientFactory clientFactory;
	private Pmp2PmpClient pmpClient;
	private Pep2PdpClient pdpClient;

	public GuiController() {
		// TODO Auto-generated constructor stub
		this.sceneGenerator = new SceneGenerator();
		this.clientFactory = new ThriftClientFactory();
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
		synchronized (this) {
			if (!this.isRunning() && !isStarted()) {
				if (start()) {
					this.setRunning(true);
					this.sceneGenerator.switchGuiCmp(true);
					this.refreshPipState();
					Platform.runLater(new Runnable() {
						public void run() {
							sceneGenerator.lab_info.setText("PDP running");
						}
					});

					if (this.clientFactory != null) {
						int pmpPort = Settings.getInstance().getPmpListenerPort();
						int pdpPort = Settings.getInstance().getPdpListenerPort();
						this.pmpClient = this.clientFactory
								.createPmp2PmpClient(new IPLocation("localhost", pmpPort));
						this.pdpClient = this.clientFactory
								.createPep2PdpClient(new IPLocation("localhost", pdpPort));
					}
				}
			}
		}
	}

	protected void stopUc() {
		synchronized (this) {
			if (this.isRunning() && isStarted()) {
				this.setRunning(false);
				this.sceneGenerator.switchGuiCmp(false);
				sceneGenerator.autoRefresh = false;
				stop();
				Platform.runLater(new Runnable() {
					public void run() {
						sceneGenerator.lab_info.setText("PDP stoppded");
					}
				});
			}
		}
	}

	public Map<String, List<String>> getDeployedMechanisms() {
		if ((this.pmpClient != null) && isRunning())
			return this.pmpClient.listMechanismsPmp();
		return null;
	}

	public void refreshPipState() {

		// Thread t = new Thread() {
		// @Override
		// public void run() {
		// while (true) {
		if (isRunning()) {
			this.sceneGenerator.txta_pipState.setText(_requestHandler
					.getIfModel());
			this.sceneGenerator.txta_pipState.setStyle("-fx-font-family: monospace;");
		}
		// pipInfoLabel.setText("REFRESHED!");
		// try {
		// Thread.sleep(3000);
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		//
		// }
		// }
		// };
		// t.start();
	}

	protected void sendCleanupEvent() {
		try {
			pdpClient.connect();

			IMessageFactory _messageFactory = MessageFactoryCreator
					.createMessageFactory();
			IEvent initEvent = _messageFactory.createActualEvent(
					"SchemaCleanup", null);
			IResponse resp = pdpClient.notifyEventSync(initEvent);
			if (resp.getAuthorizationAction().getEStatus()
					.equals(EStatus.ALLOW)) {
				this.refreshPipState();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
