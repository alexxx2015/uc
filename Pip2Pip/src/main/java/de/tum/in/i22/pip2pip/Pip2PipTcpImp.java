package de.tum.in.i22.pip2pip;

import de.tum.in.i22.uc.cm.out.TcpConnector;

public class Pip2PipTcpImp extends Pip2PipImp {
 	public Pip2PipTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}
}
