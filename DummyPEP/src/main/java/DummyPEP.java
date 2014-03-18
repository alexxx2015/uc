import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;

public class DummyPEP extends Thread {

	private final IPep2Pdp pdpCon;

	public DummyPEP(IPep2Pdp pdpCon) {
		this.pdpCon = pdpCon;
	}

	public IPep2Pdp getPdpCon() {
		return pdpCon;
	}



//	@Override
//	public void run() {
//		Map<String,String> eventParams = new HashMap<String,String>();
//        eventParams.put("InFileName", "/tmp/ucfoobar");
//
//		IEvent event = new EventBasic("DoDelete", eventParams, true);
//
//		IResponse resp = pdpCon.notifyEvent(event);
//		System.out.println(resp);
//
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//
//		resp = pdpCon.notifyEvent(new EventBasic("Delete", eventParams, true));
//		System.out.println(resp);
//
//	}


}
