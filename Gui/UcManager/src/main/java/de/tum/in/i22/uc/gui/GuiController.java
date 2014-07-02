package de.tum.in.i22.uc.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javafx.application.Application;
import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pmp2PmpClient;
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
		if (this.clientFactory != null) {
			int pmpPort = Settings.getInstance().getPmpListenerPort();
			int pdpPort = Settings.getInstance().getPdpListenerPort();
			this.pmpClient = this.clientFactory
					.createPmp2PmpClient(new IPLocation("localhost", pmpPort));
			this.pdpClient = this.clientFactory
					.createPep2PdpClient(new IPLocation("localhost", pdpPort));
		}
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

		if (!this.isRunning() && !isStarted()) {
			this.setRunning(true);
			this.sceneGenerator.switchGuiCmp(true);
			start();
		}
	}

	protected void stopUc() {
		if (this.isRunning() && isStarted()) {
			this.setRunning(false);
			this.sceneGenerator.switchGuiCmp(false);
			stop();
		}
	}

	public Map<String, List<String>> getDeployedMechanisms() {
		if ((this.pmpClient != null) && isRunning())
			return this.pmpClient.listMechanismsPmp();
		return null;
	}

	public void deployMechanisms(String file) {
		this.deployMechanisms(new File(file));
	}

	public void deployMechanisms(File file) {
		try {
			this.pmpClient.connect();
			StringBuilder policy = new StringBuilder();
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			while ((line = br.readLine()) != null){
				policy.append(line);
			}
			this.pmpClient.deployPolicyRawXMLPmp(policy.toString());
			// this.pmpClient.deployPolicyURIPmp(file.getAbsolutePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
