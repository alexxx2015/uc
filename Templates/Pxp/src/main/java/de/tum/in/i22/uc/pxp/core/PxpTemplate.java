package de.tum.in.i22.uc.pxp.core;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pdp.Client;
import de.tum.i22.in.uc.thrift.types.TAny2Pxp;
import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pxp.thrift.TAny2PxpThriftProcessor;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;
import de.tum.in.i22.uc.thrift.server.ThriftServer;

public class PxpTemplate {
	private static Logger _logger = LoggerFactory.getLogger(PxpTemplate.class);
	private static ThriftServer _pxpServer1,_pxpServer2;
	private static Settings _settings = Settings.getInstance();

	
	public static void main(String[] args) {
		
		/****
		 * Example class to register a new PXP server to the PDP
		 * PXP Server functionality is described in TAny2PxpThriftProcessor
		 */
		
		
		String pdpIp="localhost";
		int pdpPort=_settings.getPdpListenerPort();
		
		String pxpIp1="localhost";
		String pxpId1="myPxp1";
		String pxpDescription1="Oh Yeah!";
		int pxpPort1=_settings.getPxpListenerPort();
		
		String pxpIp2="localhost";
		String pxpId2="myPxp2";
		String pxpDescription2="Oh Yeah!";
		int pxpPort2=_settings.getPxpListenerPort()+1;
		
		
		//create new thrift server 1
		try {
			System.out.println("creating pxp server "+pxpId1 +" on port "+pxpPort1);
			_pxpServer1 = new ThriftServer(
								pxpPort1,
								new TAny2Pxp.Processor<TAny2PxpThriftProcessor>(new TAny2PxpThriftProcessor()));
		} catch (TTransportException e) {
			_logger.warn("Unable to start PXP server "+pxpId1 +": " + e);
			_pxpServer1 = null;
		}

		//create new thrift server 2
		try {
			System.out.println("creating pxp server "+pxpId2 +" on port "+pxpPort1);
			_pxpServer2 = new ThriftServer(
								pxpPort2,
								new TAny2Pxp.Processor<TAny2PxpThriftProcessor>(new TAny2PxpThriftProcessor()));
		} catch (TTransportException e) {
			_logger.warn("Unable to start PXP server "+pxpId2 +": " + e);
			_pxpServer2 = null;
		}

		
		
		//start server1
		if (_pxpServer1 != null) {
			System.out.println("starting pxp server "+pxpId1);
			new Thread(_pxpServer1).start();
		}
		
		//start server2
		if (_pxpServer2 != null) {
			System.out.println("starting pxp server "+pxpId1);
			new Thread(_pxpServer2).start();
		}
		
		//register them
		System.out.println("getting pdp handler ");
		PdpClientHandler<?> clientPdp = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", Settings.getInstance().getPdpListenerPort()));
			
		
		try {
			boolean b;
			System.out.print("connecting to pdp handler...");
			Object c=clientPdp.connect();
			System.out.println(c);
			
			System.out.print("registering pxp " +pxpId1+" ...");
			b=clientPdp.registerPxp(new PxpSpec(pxpIp1, pxpPort1, pxpId1, pxpDescription1));
			System.out.println(b);
			
			System.out.print("registering pxp " +pxpId2+" ...");
			b=clientPdp.registerPxp(new PxpSpec(pxpIp2, pxpPort2, pxpId2, pxpDescription2));
			System.out.println(b);

			System.out.print("disconnecting from pdp handler...");
			clientPdp.disconnect();
			System.out.println("OK");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//that's it

	}
}
