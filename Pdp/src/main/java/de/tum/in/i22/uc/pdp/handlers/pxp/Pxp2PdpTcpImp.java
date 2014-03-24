package de.tum.in.i22.uc.pdp.handlers.pxp;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pxp2PdpTcpImp extends Pxp2PdpImp {
 	public Pxp2PdpTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}
}
