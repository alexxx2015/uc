package de.tum.in.i22.cm.cm.out.pip;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pdp2PipTcpImp extends Pdp2PipImp {
	public Pdp2PipTcpImp(String address, int port) {
		super(new TcpConnector(address, port));
	}
}
