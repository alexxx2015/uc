package de.tum.in.i22.uc.tests.thrift;

import org.junit.Test;

import de.tum.in.i22.uc.cm.distribution.LocalLocation;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public class ThriftPrptest {

	@Test
	public void testThriftAny2PrpClient(){
		ThriftClientFactory tcf = new ThriftClientFactory();
		Location location = LocalLocation.getInstance();
		IAny2Prp prpClient = tcf.createAny2PrpClient(location);
	}
}
