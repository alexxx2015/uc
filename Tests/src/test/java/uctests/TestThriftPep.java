package uctests;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tum.in.i22.uc.Controller;
import de.tum.in.i22.uc.cm.datatypes.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IResponse;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PdpClient;
import de.tum.in.i22.uc.cm.distribution.client.Pep2PipClient;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class TestThriftPep extends GenericTest{

	@Test
	public void testThriftPep() throws IOException {
		sayMyName(Thread.currentThread().getStackTrace()[1].getMethodName());

		/*
		 * This test assumes that the 'controller' is already running
		 * and listening on the predefined ports.
		 */
		IPLocation pdpLocation = new IPLocation("localhost", PDP_SERVER_PORT);
		IPLocation pipLocation = new IPLocation("localhost", PIP_SERVER_PORT);

		Pep2PdpClient toPdp = new ThriftClientFactory().createPep2PdpClient(pdpLocation);
		toPdp.connect();

		Pep2PipClient toPip = new ThriftClientFactory().createPep2PipClient(pipLocation);
		toPip.connect();

		IResponse r = toPdp.notifyEventSync(new EventBasic("foo", new HashMap<String,String>()));

		Assert.assertEquals(EStatus.ALLOW, r.getAuthorizationAction().getEStatus());
	}

}
