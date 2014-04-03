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
	public PdpClientHandler<?> createPdpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPdpClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public PipClientHandler<?> createPipClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPipClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public PmpClientHandler<?> createPmpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPmpClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public PxpClientHandler<?> createPxpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftPxpClientHandler((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

}
