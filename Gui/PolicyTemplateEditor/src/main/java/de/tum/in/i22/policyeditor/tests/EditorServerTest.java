package de.tum.in.i22.policyeditor.tests;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.policyeditor.server.EditorThriftServer;
import de.tum.in.i22.policyeditor.server.TAny2Editor;
import de.tum.in.i22.uc.thrift.types.TAttribute;
import de.tum.in.i22.uc.thrift.types.TAttributeName;
import de.tum.in.i22.uc.thrift.types.TContainer;

public class EditorServerTest {
	
	private static TTransport transport;
	private static TAny2Editor.Client client;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		 try {
			 int port = EditorThriftServer.getPort();
			 			 
		      transport = new TSocket("localhost", port);
		      transport.open();

		      TProtocol protocol = new  TBinaryProtocol(transport);
		      client = new TAny2Editor.Client(protocol);
		 } catch (TException x) {
		      x.printStackTrace();
		 }
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		transport.close();
	}
	
	@Test
	public void launchEditorSimple() {
		String dataClass = "city";
		Set<TContainer> representations = getTestContainers();
		try {
			client.specifyPolicyFor(representations, dataClass);
		} catch (TException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		assertTrue(true);
	}
	
	
	private Set<TContainer> getTestContainers(){
		Set<TContainer> representations = new HashSet(); 
		for(int i =0; i<3; i++){
			TContainer c = new TContainer();
			c.setId("container_"+i);
			TAttribute attr = new TAttribute();
			TAttributeName name = TAttributeName.OWNER;
			attr.setName(name);
			attr.setValue("attr"+i);
			c.addToAttributes(attr);
			representations.add(c);
		}
		return representations;
	}
	
}
