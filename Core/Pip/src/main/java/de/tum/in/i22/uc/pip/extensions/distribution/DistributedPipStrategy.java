package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.tum.in.i22.uc.cm.client.ClientHandlerFactory;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.distribution.AbstractStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.pip.EDistributedPipStrategy;
import de.tum.in.i22.uc.cm.distribution.pip.IDistributedPipStrategy;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.thrift.client.ThriftClientHandlerFactory;

public abstract class DistributedPipStrategy extends AbstractStrategy implements IDistributedPipStrategy {

	private final EDistributedPipStrategy _eStrategy;

	protected final ClientHandlerFactory _clientHandlerFactory;
	
	protected Map<Location,PipClientHandler> _pipClientHandlers;

	public DistributedPipStrategy(EDistributedPipStrategy eStrategy) {
		_eStrategy = eStrategy;
		_pipClientHandlers = new HashMap<>();

		switch (Settings.getInstance().getCommunicationProtocol()) {
			case THRIFT:
				_clientHandlerFactory = new ThriftClientHandlerFactory();
				break;
			default:
				throw new RuntimeException("Unsupported communication protocol.");
		}
	}

	@Override
	public final EDistributedPipStrategy getStrategy() {
		return _eStrategy;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj instanceof DistributedPipStrategy) {
			return Objects.equals(_eStrategy, ((DistributedPipStrategy) obj)._eStrategy);
		}
		return false;
	}

	static final DistributedPipStrategy create(EDistributedPipStrategy strategy) {
		switch (strategy) {
		case PUSH:
			return new PipPushStrategy(strategy);
		}

		throw new RuntimeException("No such DistributedPipStrategy: " + strategy);
	}
}
