package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.distribution.Location;

public interface ClientHandlerFactory {
	Any2PdpClient createPdpClientHandler(Location location);
	Any2PipClient createPipClientHandler(Location location);
	Any2PmpClient createPmpClientHandler(Location location);
	Any2PxpClient createPxpClientHandler(Location location);
}
