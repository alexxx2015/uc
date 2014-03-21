package de.tum.in.i22.uc.pdp.handlers.pep;

import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pep2PdpTcpImp extends Pep2PdpImp implements IPep2Pdp {
 	public Pep2PdpTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}
}
