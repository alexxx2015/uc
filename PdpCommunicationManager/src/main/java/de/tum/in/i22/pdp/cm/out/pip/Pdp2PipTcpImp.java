package de.tum.in.i22.pdp.cm.out.pip;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pdp2PipTcpImp extends Pdp2PipImp implements IPdp2PipTcp {
	public Pdp2PipTcpImp(String address, int port) {
		super(new TcpConnector(address, port));
	}
}
