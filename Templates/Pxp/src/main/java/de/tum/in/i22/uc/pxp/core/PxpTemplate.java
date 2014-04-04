package de.tum.in.i22.uc.pxp.core;

import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.basic.PxpSpec;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;
import de.tum.in.i22.uc.thrift.server.IThriftServer;
import de.tum.in.i22.uc.thrift.server.ThriftProcessorFactory;
import de.tum.in.i22.uc.thrift.types.TAny2Pxp;

public class PxpTemplate {
	private static Logger _logger = LoggerFactory.getLogger(PxpTemplate.class);
	private static IThriftServer _pxpServer1,_pxpServer2;
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
		System.out.println("creating pxp server "+pxpId1 +" on port "+pxpPort1);
		_pxpServer1 = ThriftProcessorFactory.createPxpThriftServer(pxpPort1);

		//create new thrift server 2
		System.out.println("creating pxp server "+pxpId2 +" on port "+pxpPort2);
		_pxpServer2 = ThriftProcessorFactory.createPxpThriftServer(pxpPort2);


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
		PdpClientHandler clientPdp = new ThriftClientHandlerFactory().createPdpClientHandler(new IPLocation("localhost", Settings.getInstance().getPdpListenerPort()));


		try {
			boolean b;
			System.out.print("connecting to pdp handler...");
			clientPdp.connect();

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
