package de.tum.in.i22.uc.tests.thrift;

import java.io.IOException;
import java.util.HashMap;

import org.junit.Test;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.NewThriftPep2PdpClient;

public class MyTest {
	@Test
	public void mytest() {
		NewThriftPep2PdpClient x = new NewThriftPep2PdpClient("localhost", Settings.getInstance().getPdpListenerPort());
		try {
			x.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		IResponse r = x.notifyEventSync(new EventBasic("foo", new HashMap<String,String>()));
		System.out.println(r);
	}
}
