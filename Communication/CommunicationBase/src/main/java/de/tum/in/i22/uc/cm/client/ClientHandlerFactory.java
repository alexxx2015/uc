package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPdp2Pxp;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPep2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPip2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPip2Pmp;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pdp;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pip;
import de.tum.in.i22.uc.cm.interfaces.IPmp2Pmp;

public interface ClientHandlerFactory {
	Any2PdpClient createPdpClientHandler(Location location);
	Any2PipClient createPipClientHandler(Location location);
	Any2PmpClient createPmpClientHandler(Location location);
	Any2PxpClient createPxpClientHandler(Location location);

	Pep2PipClient createPep2PipClient(Location location);
	Pdp2PipClient createPdp2PipClient(Location location);
	Pip2PipClient createPip2PipClient(Location location);
	Pmp2PipClient createPmp2PipClient(Location location);

	Pep2PdpClient createPep2PdpClient(Location location);
	Pmp2PdpClient createPmp2PdpClient(Location location);

	Pip2PmpClient createPip2PmpClient(Location location);
	Pmp2PmpClient createPmp2PmpClient(Location location);

	Pdp2PxpClient createPdp2PxpClient(Location location);
}
