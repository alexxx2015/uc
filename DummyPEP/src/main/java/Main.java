import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
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
				IEvent ev;
				IResponse resp;

		        ev = createSocketEvent("H1", "1000", "4");
		        resp = getPdpCon().notifyEvent(ev);
				System.out.println(resp);

		        ev = createSocketEvent("H2", "2000", "5");
		        resp = getPdpCon().notifyEvent(ev);
				System.out.println(resp);

				/**
				 * Important note:
				 * The PEP must enforce that the connect() happens before the corresponding accept!
				 */

				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(resp);

				ev = createAcceptEvent("H2", "2000", "192.168.0.1", "5005", "192.168.0.1", "5000", "AF_INET", "6");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(resp);
			}
		}.start();
	}

	private static EventBasic createEvent(String name, Map<String,String> params) {
        params.put("PEP", "Linux");
        return new EventBasic(name, params, true);
	}

	private static IEvent createAcceptEvent(String host, String pid, String localIP, String localPort, String remoteIp, String remotePort, String family, String newfd) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("localIP", localIP);
		params.put("localPort", localPort);
		params.put("remoteIP", remoteIp);
		params.put("remotePort", remotePort);
		params.put("family", family);
        params.put("host", host);
        params.put("pid", pid);
        params.put("newfd", newfd);

		return createEvent("Accept", params);
	}

	private static IEvent createConnectEvent(String host, String pid, String fd, String localIP, String localPort, String remoteIp, String remotePort, String family) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("localIP", localIP);
		params.put("localPort", localPort);
		params.put("remoteIP", remoteIp);
		params.put("remotePort", remotePort);
		params.put("family", family);
        params.put("host", host);
        params.put("pid", pid);
        params.put("fd", fd);

		return createEvent("Connect", params);
	}

	private static IEvent createSocketEvent(String host, String pid, String fd) {
		Map<String,String> params = new HashMap<String,String>();
        params.put("host", host);
        params.put("pid", pid);
        params.put("fd", fd);

        return createEvent("Socket", params);
	}
}
