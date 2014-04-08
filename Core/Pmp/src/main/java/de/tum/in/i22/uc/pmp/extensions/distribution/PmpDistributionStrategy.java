package de.tum.in.i22.uc.pmp.extensions.distribution;

import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.ConnectionManager;
import de.tum.in.i22.uc.cm.client.PmpClientHandler;
import de.tum.in.i22.uc.cm.distribution.AbstractDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;

public abstract class PmpDistributionStrategy extends AbstractDistributionStrategy implements IPmpDistributionStrategy {

	protected final ClientHandlerFactory _clientHandlerFactory;

	protected final ConnectionManager<PmpClientHandler> _connectionManager;

	public PmpDistributionStrategy(EDistributionStrategy eStrategy) {
		super(eStrategy);

		_connectionManager = new ConnectionManager<>(Settings.getInstance().getPmpDistributionMaxConnections());

		switch (Settings.getInstance().getCommunicationProtocol()) {
			case THRIFT:
				_clientHandlerFactory = new ThriftClientHandlerFactory();
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
