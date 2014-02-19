import java.util.HashMap;
import java.util.Map;

import de.tum.in.i22.pdp.core.IPep2Pdp;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.out.IFastConnector;


public class DummyPEP extends Thread {
	
	private Pep2PdpFastImp pdpCon;
	
	public DummyPEP(Pep2PdpFastImp pdpCon) {
		this.pdpCon = pdpCon;
	}
	
	@Override
	public void run() {				
		Map<String,String> eventParams = new HashMap<String,String>();
        eventParams.put("InFileName", "/tmp/ucfoobar");
                
		IEvent event = new EventBasic("DoDelete", eventParams, true);
		
		IResponse resp = pdpCon.notifyEvent(event);
		System.out.println(resp);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		resp = pdpCon.notifyEvent(new EventBasic("Delete", eventParams, true));
		System.out.println(resp);
		
	}
}
