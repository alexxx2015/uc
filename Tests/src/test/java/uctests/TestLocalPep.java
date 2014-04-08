package uctests;

import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.client.Pep2AnyClient;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.handlers.RequestHandler;
import de.tum.in.i22.uc.cm.server.IRequestHandler;

public class TestLocalPep {

	@Test
	public void testLocalPep() {
		IRequestHandler rq = new RequestHandler(new LocalLocation(), new LocalLocation(), new LocalLocation());
		Pep2AnyClient pep = new Pep2AnyClient(rq, rq);

		IResponse r = pep.notifyEventSync(new EventBasic("foo", new HashMap<String,String>()));

		Assert.assertEquals(EStatus.ALLOW, r.getAuthorizationAction().getEStatus());
	}
}
