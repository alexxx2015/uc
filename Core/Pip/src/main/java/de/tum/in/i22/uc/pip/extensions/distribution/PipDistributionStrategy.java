package de.tum.in.i22.uc.pip.extensions.distribution;

import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.ConnectionManager;
import de.tum.in.i22.uc.cm.client.Any2PipClient;
import de.tum.in.i22.uc.cm.distribution.AbstractDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public abstract class PipDistributionStrategy extends AbstractDistributionStrategy implements IPipDistributionStrategy  {

	protected final ClientHandlerFactory _clientHandlerFactory;

	protected final ConnectionManager<Any2PipClient> _connectionManager;

	public PipDistributionStrategy(EDistributionStrategy eStrategy) {
		super(eStrategy);

		_connectionManager = new ConnectionManager<>(Settings.getInstance().getPipDistributionMaxConnections());

		switch (Settings.getInstance().getCommunicationProtocol()) {
			case THRIFT:
				_clientHandlerFactory = new ThriftClientFactory();
				break;
			default:
				throw new RuntimeException("Unsupported communication protocol.");
		}
	}

	static final PipDistributionStrategy create(EDistributionStrategy strategy) {
		switch (strategy) {
			case PUSH:
				return new PipPushStrategy(strategy);
		}

		throw new RuntimeException("No such " + EDistributionStrategy.class.getSimpleName() + ": " + strategy);
	}
}
