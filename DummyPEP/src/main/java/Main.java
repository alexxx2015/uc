import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import de.tum.in.i22.pep2pdp.Pep2PdpTcpImp;
import de.tum.in.i22.pep2pdp.Pep2PdpPipeImp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.out.ConnectionManager;


public class Main {
	public static final String PDP1_HOST = "localhost";
	public static final int PDP1_PORT = 51001;

	public static final String PDP2_HOST = "localhost";
	public static final int PDP2_PORT = 52001;

	public static IPep2Pdp connectPDP(String host, int port) {
		return new Pep2PdpTcpImp(host, port);
	}

	public static IPep2Pdp connectPipePDP(String inPipe, String outPipe) {
		return new Pep2PdpPipeImp(new File(inPipe), new File(outPipe));
	}

	public static void main(String[] args) throws IOException {
//		Pep2PdpPipeImp pdp1 = connectPipePDP("/tmp/pdp2pep", "/tmp/pep2pdp");
		IPep2Pdp pdp1 = connectPDP(PDP1_HOST, PDP1_PORT);
//		Pep2PdpFastImp pdp2 = connectPDP(PDP2_HOST, PDP2_PORT);

		pdp1 = ConnectionManager.obtain(pdp1);

		new DummyPEP(pdp1) {
			@Override
			public void run() {
				IEvent ev;
				IResponse resp;

		        ev = createSocketEvent("H1", "1000", "4");
		        resp = getPdpCon().notifyEvent(ev);

		        ev = createSocketEvent("H2", "2000", "5");
		        resp = getPdpCon().notifyEvent(ev);

				System.out.println(resp);


				/**
				 * Important note:
				 * The PEP must enforce that the connect() happens before the corresponding accept!
				 */

		        Stopwatch w = Stopwatch.createUnstarted();
		        w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();
				w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();
				w.start();

				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();

				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();

				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();

				ev = createAcceptEvent("H2", "2000", "192.168.0.1", "5005", "192.168.0.1", "5000", "AF_INET", "6");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));
				w.reset();w.start();

				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));


				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));


				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));

				w.reset();w.start();
				ev = createConnectEvent("H1", "1000", "4", "192.168.0.1", "5000", "192.168.0.1", "5005", "AF_INET");
				resp = getPdpCon().notifyEvent(ev);
				System.out.println(w.stop().elapsed(TimeUnit.MICROSECONDS));


				IPep2Pdp con = this.getPdpCon();
				try {
					ConnectionManager.release(con);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
