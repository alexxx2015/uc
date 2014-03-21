package de.tum.in.i22.pdp.handlers.pmp;

import de.tum.in.i22.uc.cm.out.TcpConnector;

/**
 *
 * @author Florian Kelbert
 *
 */
public class Pmp2PdpTcpImp extends Pmp2PdpImp {
 	public Pmp2PdpTcpImp(String address, int port) {
 		super(new TcpConnector(address, port));
	}
}
