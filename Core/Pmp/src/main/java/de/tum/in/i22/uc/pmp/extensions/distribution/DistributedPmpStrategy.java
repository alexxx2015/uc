package de.tum.in.i22.uc.pmp.extensions.distribution;

import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.ConnectionManager;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.distribution.AbstractStrategy;
import de.tum.in.i22.uc.cm.distribution.EDistributedStrategy;
import de.tum.in.i22.uc.cm.distribution.IDistributedPmpStrategy;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;

public abstract class DistributedPmpStrategy extends AbstractStrategy implements IDistributedPmpStrategy {

	protected final ClientHandlerFactory _clientHandlerFactory;

	protected final ConnectionManager<PipClientHandler> _connectionManager;

	public DistributedPmpStrategy(EDistributedStrategy eStrategy) {
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

	static final DistributedPmpStrategy create(EDistributedStrategy strategy) {
		switch (strategy) {
			case PUSH:
				return new PmpPushStrategy(strategy);
		}

		throw new RuntimeException("No such DistributedPipStrategy: " + strategy);
	}
}
