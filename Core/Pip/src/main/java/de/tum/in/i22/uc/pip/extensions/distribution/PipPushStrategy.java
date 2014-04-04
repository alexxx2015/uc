package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.pip.EDistributedPipStrategy;

public class PipPushStrategy extends DistributedPipStrategy {
	protected static final Logger _logger = LoggerFactory.getLogger(PipPushStrategy.class);

	public PipPushStrategy(EDistributedPipStrategy eStrategy) {
		super(eStrategy);
	}

	@Override
	public boolean hasAllData(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Location location, Set<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Location location, Set<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Location location, Set<IContainer> containers) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus initialRepresentation(Location location, IName containerName, Set<IData> data) {
		_logger.debug("initialRepresentation(" + location + "," + containerName + "," + data + ")");

		PipClientHandler pip = _pipClientHandlers.get(location);

		if (pip == null) {
			pip = _clientHandlerFactory.createPipClientHandler(location);
			try {
				pip.connect();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		IStatus result = pip.initialRepresentation(containerName, data);
//		pip.disconnect();

		return result;
	}

	@Override
	public IStatus update(Location location, IEvent event) {
		_logger.debug("update(" + location + "," + event + ")");

//		PipClientHandler pip = _clientHandlerFactory.createPipClientHandler(location);
//		try {
//			pip.connect();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		IStatus result = pip.update(event);
//		pip.disconnect();
//		return result;
		return null;
	}
}
