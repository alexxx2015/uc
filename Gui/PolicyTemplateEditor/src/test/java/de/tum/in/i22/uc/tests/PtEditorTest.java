package de.tum.in.i22.uc.tests;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.policyeditor.server.PtEditorHandler;
import de.tum.in.i22.uc.cm.datatypes.basic.AttributeBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.AttributeBasic.EAttributeName;
import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IAttribute;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.interfaces.IAny2PtEditor;
import de.tum.in.i22.uc.thrift.client.ThriftAny2PtEditorClient;
import de.tum.in.i22.uc.thrift.server.TAny2PtEditorThriftServer;

public class PtEditorTest {

	/**
	 * 
	 */
	private static final boolean TESTS_ENABLED = false;
	private static Logger _logger = LoggerFactory.getLogger(PtEditorTest.class);
	
	private static IAny2PtEditor clientEditor;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if(!TESTS_ENABLED){
			return;
		}
		/*
		 * Start PtEditor server.
		 */
		int PORT = 51011;
		String host = "127.0.0.1";
		PtEditorHandler editorHandler = new PtEditorHandler();
		TAny2PtEditorThriftServer editorServer = new TAny2PtEditorThriftServer(PORT, editorHandler);
		editorServer.startEditorServer();
		
		/*
		 * Start PtEditor client.
		 * */
		
		 clientEditor = new ThriftAny2PtEditorClient(host, PORT);
		 ((ThriftAny2PtEditorClient) clientEditor).connect();
		 
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testLauchEditor() {
		if(!TESTS_ENABLED){
			return;
		}
		String dataClass = "city";
		Set<IContainer> representations = getTestContainers();
		clientEditor.specifyPolicyFor(representations, dataClass);
		
		assertTrue(true);
	}

	private Set<IContainer> getTestContainers(){
		Set<IContainer> representations = new HashSet(); 
		for(int i =0; i<3; i++){
			ArrayList<IAttribute> attributes = new ArrayList<>();
			IAttribute attr = new AttributeBasic(EAttributeName.OWNER, "owner"+i);
			attributes.add(attr);
			ContainerBasic c = new ContainerBasic("container_"+i, attributes);
			representations.add(c);
		}
		return representations;
	}
	
}
