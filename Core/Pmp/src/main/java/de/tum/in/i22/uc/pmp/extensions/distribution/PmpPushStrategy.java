package de.tum.in.i22.uc.pmp.extensions.distribution;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;

public class PmpPushStrategy extends PmpDistributionStrategy {
	protected static final Logger _logger = LoggerFactory.getLogger(PmpPushStrategy.class);

	public PmpPushStrategy(EDistributionStrategy eStrategy) {
		super(eStrategy);
	}

	@Override
	public IStatus doRemotePolicyTransfer(Location location, Set<String> policies) {
		//		_logger.info("Performing remote policy transfer: " + policies);
		//
		//		try {
		//			Any2PmpClient _pmpHandle = _connectionManager.obtain(_clientHandlerFactory.createAny2PmpClient(location));
		//			_pmpHandle.deployPolicy(policies);
		//			_connectionManager.release(_pmpHandle);
		//		}
		//		catch (IOException e) {
		//			_logger.warn("Unable to perform remote data transfer: " + e + System.lineSeparator());
		//			return new StatusBasic(EStatus.ERROR, "Unable to perform remote data transfer: " + e);
		//		}

		return new StatusBasic(EStatus.OKAY);
	}
}