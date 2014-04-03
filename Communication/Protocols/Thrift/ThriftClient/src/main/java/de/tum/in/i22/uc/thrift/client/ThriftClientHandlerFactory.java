package de.tum.in.i22.uc.thrift.client;

import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.PdpClientHandler;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.client.PmpClientHandler;
import de.tum.in.i22.uc.cm.client.PxpClientHandler;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;


public class ThriftClientHandlerFactory implements ClientHandlerFactory {

	@Override
	public ThriftPdpClientHandler createPdpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdpClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public ThriftPipClientHandler createPipClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPipClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public ThriftPmpClientHandler createPmpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmpClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public ThriftPxpClientHandler createPxpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPxpClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

}
