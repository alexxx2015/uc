package de.tum.in.i22.uc.thrift.client;

import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.Any2PdpClient;
import de.tum.in.i22.uc.cm.client.Any2PipClient;
import de.tum.in.i22.uc.cm.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.client.Any2PxpClient;
import de.tum.in.i22.uc.cm.distribution.IPLocation;
import de.tum.in.i22.uc.cm.distribution.Location;


public class ThriftClientFactory implements ClientHandlerFactory {

	@Override
	public Any2PdpClient createPdpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PdpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PipClient createPipClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PipClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PmpClient createPmpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PmpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}

	@Override
	public Any2PxpClient createPxpClientHandler(Location location) {
		if (location instanceof IPLocation) {
			return new ThriftAny2PxpClient((IPLocation) location);
		}
		throw new RuntimeException("Thrift client depends on " + IPLocation.class + ", but got " + location.getClass());
	}
}
