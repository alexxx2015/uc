package de.tum.in.i22.pep2pdp;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pep2PdpTcpImp extends Pep2PdpImp {
 	public Pep2PdpTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}
}
