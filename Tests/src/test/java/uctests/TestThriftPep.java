package uctests;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.client.Pep2PipClient;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class TestThriftPep {

	@BeforeClass
	public static void setUpClass() {
		Controller.start();
	}

	@Test
	public void test() throws IOException {
		/*
		 * This test assumes that the 'controller' is already running
		 * and listening on the predefined ports.
		 */
		IPLocation pdpLocation = new IPLocation("localhost", Settings.getInstance().getPdpListenerPort());
		IPLocation pipLocation = new IPLocation("localhost", Settings.getInstance().getPipListenerPort());

		Pep2PdpClient toPdp = new ThriftClientFactory().createPep2PdpClient(pdpLocation);
		toPdp.connect();

		Pep2PipClient toPip = new ThriftClientFactory().createPep2PipClient(pipLocation);
		toPip.connect();

		IResponse r = toPdp.notifyEventSync(new EventBasic("foo", new HashMap<String,String>()));

		Assert.assertEquals(EStatus.ALLOW, r.getAuthorizationAction().getEStatus());
	}

}
