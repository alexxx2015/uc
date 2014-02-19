import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.out.IFastConnector;


public class Main {
	public static final String PDP_HOST = "localhost";
	public static final int PDP_PORT = 50001;

	public static void main(String[] args) {
		Pep2PdpFastImp pdpProxy = new Pep2PdpFastImp(PDP_HOST, PDP_PORT);
        try {
			pdpProxy.connect();
		} catch (Exception e) {
			System.err.println("Unable to connect to PDP. Exiting.");
			return;
		} 
		
//		DummyPEP pep1 = new DummyPEP(pdpProxy);
//		pep1.start();
//		
		new DummyPEP(pdpProxy) {
			@Override
			public void run() {
				Map<String,String> eventParams = new HashMap<String,String>();
		        eventParams.put("InFileName", "/tmp/ucfoobar");
				
				IResponse resp = getPdpCon().notifyEvent(new EventBasic("DoDelete", eventParams, true));
				System.out.println(resp);				
			}
		}.start();
		
		new DummyPEP(pdpProxy) {
			@Override
			public void run() {
				Map<String,String> eventParams = new HashMap<String,String>();
		        eventParams.put("InFileName", "/tmp/ucfoobar");

				
				try {
					Thread.sleep(6000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				IResponse resp = getPdpCon().notifyEvent(new EventBasic("Delete", eventParams, true));
				System.out.println(resp);
			}
		}.start();
	}

}
