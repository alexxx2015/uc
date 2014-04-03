package de.tum.in.i22.uc.cm.client;

import de.tum.in.i22.uc.cm.distribution.Location;

public interface ClientHandlerFactory {
	PdpClientHandler createPdpClientHandler(Location location);
	PipClientHandler createPipClientHandler(Location location);
	PmpClientHandler createPmpClientHandler(Location location);
	PxpClientHandler createPxpClientHandler(Location location);
}
