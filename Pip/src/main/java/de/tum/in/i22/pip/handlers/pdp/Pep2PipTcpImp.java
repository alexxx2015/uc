package de.tum.in.i22.pip.handlers.pdp;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pep2PipTcpImp extends Pep2PipImp  {
 	public Pep2PipTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}

}
