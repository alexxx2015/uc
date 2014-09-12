package de.tum.in.i22.policyeditor.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.policyeditor.deployment.DeploymentController;
import de.tum.in.i22.policyeditor.editor.PolicyTemplatesEditor;
import de.tum.in.i22.policyeditor.util.Config;
import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.interfaces.IAny2PtEditor;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

/**
 * @author cipri
 *
 */
public class PtEditorHandler implements IAny2PtEditor {

	private static final Logger _logger = LoggerFactory.getLogger(PtEditorHandler.class);
	
	private static ThriftClientFactory thriftClientFactory = new ThriftClientFactory();
	private Any2PmpClient clientPmp;
	private String defaultPmp2PdpIp = "localhost";
	private int defaultPmp2PdpPort = 20001;
	
	
	public PtEditorHandler(){
		clientPmp = null;
	}

	@Override
	public IStatus specifyPolicyFor(Set<IContainer> representations, String dataClass) {
		
		boolean connected = initializePMPconnection();
		if(!connected)
			return new StatusBasic(EStatus.ERROR);
		
		DeploymentController deploymentController = new DeploymentController(clientPmp);
		
		PolicyTemplatesEditor.startEditor(deploymentController, representations, dataClass);
		
		return new StatusBasic(EStatus.OKAY);
	}

	private boolean initializePMPconnection(){
		
		String host = defaultPmp2PdpIp;
		int port = defaultPmp2PdpPort;
		try {
			Config config = new Config();
			String portString=config.getProperty("policyManagementPort");
			port = Integer.parseInt(portString);
			String hostString = config.getProperty("policyManagementHost");
			host = hostString;
		} catch (Exception e) {
		}
		
		try{ 
			clientPmp=thriftClientFactory.createAny2PmpClient(new IPLocation(host, port));
			clientPmp.connect();
			String logMsg = "PolicyTemplatesEditor connected to PMP - "+ host+":"+port;
			_logger.info(logMsg);
		} 
		catch(IOException ex){
			_logger.error("PolicyTemplatesEditor failed to connected to PMP - "+ host+":"+port, ex);
			return true;
		}
		return true;
	}
	
	/*
	 * This Editor listener must be started manually.
	 ************************************************* 
	 */
	
	public static void main(String[] args){
		PtEditorHandler editorHandler = new PtEditorHandler();
		
		String dataClass = "picture";
		Set<IContainer> representations = new HashSet<>();
		
		ContainerBasic container = new ContainerBasic("container1");
		representations.add(container);
		
		editorHandler.specifyPolicyFor(representations, dataClass);
	}
}