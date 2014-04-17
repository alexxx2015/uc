package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.distribution.AbstractDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.client.ConnectionManager;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;
import de.tum.in.i22.uc.cm.factories.IClientFactory;
import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel;
import de.tum.in.i22.uc.pip.core.ifm.InformationFlowModelManager;
import de.tum.in.i22.uc.thrift.client.ThriftClientFactory;

public abstract class PipDistributionStrategy extends AbstractDistributionStrategy implements IPipDistributionStrategy  {

	protected final IClientFactory _clientHandlerFactory;

	protected final ConnectionManager<Pip2PipClient> _connectionManager;

	protected final BasicInformationFlowModel _ifModel;

	public PipDistributionStrategy(EDistributionStrategy eStrategy) {
		super(eStrategy);

		_connectionManager = new ConnectionManager<>(Settings.getInstance().getPipDistributionMaxConnections());

		_ifModel = InformationFlowModelManager.getInstance().getBasicInformationFlowModel();

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
