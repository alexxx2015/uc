package de.tum.in.i22.uc.pip.handlers.pip;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pip2PipTcpImp extends Pip2PipImp {
 	public Pip2PipTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}
}
