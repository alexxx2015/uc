package de.tum.in.i22.uc.pmp.extensions.distribution;

import de.tum.in.i22.uc.cm.distribution.AbstractDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.client.Any2PmpClient;
import de.tum.in.i22.uc.cm.distribution.client.ConnectionManager;
import de.tum.in.i22.uc.cm.factories.IClientHandlerFactory;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public abstract class PmpDistributionStrategy extends AbstractDistributionStrategy implements IPmpDistributionStrategy {

	protected final IClientHandlerFactory _clientHandlerFactory;

	protected final ConnectionManager<Any2PmpClient> _connectionManager;

	public PmpDistributionStrategy(EDistributionStrategy eStrategy) {
		super(eStrategy);

		_connectionManager = new ConnectionManager<>(Settings.getInstance().getPmpDistributionMaxConnections());

		switch (Settings.getInstance().getCommunicationProtocol()) {
			case THRIFT:
				_clientHandlerFactory = new ThriftClientFactory();
				break;
			default:
				throw new RuntimeException("Unsupported communication protocol.");
		}
	}

	static final PmpDistributionStrategy create(EDistributionStrategy strategy) {
		switch (strategy) {
			case PUSH:
				return new PmpPushStrategy(strategy);
		}

		throw new RuntimeException("No such " + EDistributionStrategy.class.getSimpleName() + ": " + strategy);
	}

}
