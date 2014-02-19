import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
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
		
		DummyPEP pep1 = new DummyPEP(pdpProxy);
		pep1.start();
	}

}
