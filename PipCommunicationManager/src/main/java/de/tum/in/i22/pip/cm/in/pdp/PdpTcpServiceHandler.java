package de.tum.in.i22.pip.cm.in.pdp;

import java.io.IOException;
import java.net.Socket;

import de.tum.in.i22.uc.cm.in.TcpServiceHandler;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;

public class PdpTcpServiceHandler extends TcpServiceHandler {
	private final IPdp2Pip _pdp2Pip;

	public PdpTcpServiceHandler(int port, IPdp2Pip pdp2Pip) {
		super(port);
		_pdp2Pip = pdp2Pip;
	}

	@Override
	protected void doHandleClientConnection(Socket clientSocket) throws IOException {
		PdpClientConnectionHandler pdpClientConnHandler =
				new PdpClientConnectionHandler(clientSocket, _pdp2Pip);
		// invoke run directly (do not create separate thread)
		// this means that the requests from PDP will be served sequentially
		pdpClientConnHandler.run();
	}

	@Override
	protected String getServerInfo() {
		return "PDPlistener";
	}

}
