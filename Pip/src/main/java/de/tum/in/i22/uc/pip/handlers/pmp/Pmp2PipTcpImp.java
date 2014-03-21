package de.tum.in.i22.uc.pip.handlers.pmp;

import de.tum.in.i22.uc.cm.out.TcpConnector;

/**
 *
 * @author Florian Kelbert
 *
 */
public class Pmp2PipTcpImp extends Pmp2PipImp {
	public Pmp2PipTcpImp(String address, int port) {
		super(new TcpConnector(address, port));
	}
}
