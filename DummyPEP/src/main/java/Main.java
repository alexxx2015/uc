import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IResponse;


public class Main {
	public static final String PDP1_HOST = "localhost";
	public static final int PDP1_PORT = 51001;

	public static final String PDP2_HOST = "localhost";
	public static final int PDP2_PORT = 52001;

	public static Pep2PdpFastImp connectPDP(String host, int port) {
		Pep2PdpFastImp pdp = new Pep2PdpFastImp(host, port); try {
			pdp.connect();
		} catch (Exception e) {
			System.err.println("Unable to connect to PDP(" + host + "," + port + "). Exiting.");
			System.exit(0);
		}

		return pdp;
	}

	public static void main(String[] args) {
		Pep2PdpFastImp pdp1 = connectPDP(PDP1_HOST, PDP1_PORT);
//		Pep2PdpFastImp pdp2 = connectPDP(PDP2_HOST, PDP2_PORT);

		new DummyPEP(pdp1) {
			@Override
			public void run() {
				Map<String,String> eventParams = new HashMap<String,String>();
		        eventParams.put("host", "machine1");
		        eventParams.put("pid", "4251");
		        eventParams.put("fd", "4");
		        eventParams.put("PEP", "Linux");

		        IResponse resp = getPdpCon().notifyEvent(new EventBasic("Socket", eventParams, true));
				System.out.println(resp);


				eventParams.clear();
		        eventParams.put("localIP", "192.168.0.1");
		        eventParams.put("localPort", "234");
		        eventParams.put("remoteIP", "192.168.0.2");
		        eventParams.put("remotePort", "345");
		        eventParams.put("family", "AF_INET");
		        eventParams.put("host", "machine1");
		        eventParams.put("pid", "4251");
		        eventParams.put("fd", "4");
		        eventParams.put("PEP", "Linux");

				resp = getPdpCon().notifyEvent(new EventBasic("Connect", eventParams, true));
				System.out.println(resp);
			}
		}.start();

//		new DummyPEP(pdp2) {
//			@Override
//			public void run() {
//				Map<String,String> eventParams = new HashMap<String,String>();
//		        eventParams.put("InFileName", "/tmp/ucfoobar");
//
//
//				try {
//					Thread.sleep(6000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//
//				IResponse resp = getPdpCon().notifyEvent(new EventBasic("Delete", eventParams, true));
//				System.out.println(resp);
//			}
//		}.start();
	}

}
