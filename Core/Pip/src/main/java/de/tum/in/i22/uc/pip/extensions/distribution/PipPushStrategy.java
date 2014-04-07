package de.tum.in.i22.uc.pip.extensions.distribution;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.client.PipClientHandler;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.EDistributedStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;

public class PipPushStrategy extends DistributedPipStrategy {
	protected static final Logger _logger = LoggerFactory.getLogger(PipPushStrategy.class);

	public PipPushStrategy(EDistributedStrategy eStrategy) {
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
	public IStatus remoteEventUpdate(Location location, IEvent event) {
		_logger.debug("remoteEventUpdate(" + location + "," + event + ")");

		try {
			PipClientHandler pipHandle = _connectionManager.obtain(_clientHandlerFactory.createPipClientHandler(location));

			pipHandle.update(event);

			_connectionManager.release(pipHandle);
		} catch (IOException e) {
			_logger.warn("remoteEventUpdate failed: " + e.getMessage());
			return new StatusBasic(EStatus.ERROR, e.getMessage());
		}

		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus remoteDataFlow(Map<Location, Map<IName, Set<IData>>> dataflow) {
		_logger.info("Performing remote data flow transfer: " + dataflow);

		String errorString = "";
		for (Location location : dataflow.keySet()) {
			try {
				PipClientHandler _pipHandle = _connectionManager.obtain(_clientHandlerFactory.createPipClientHandler(location));

				// TODO: Update Thrift to get rid of this loop. Possible?
				for (Entry<IName,Set<IData>> entry : dataflow.get(location).entrySet()) {
					_pipHandle.initialRepresentation(entry.getKey(), entry.getValue());
				}

				_connectionManager.release(_pipHandle);

			} catch (IOException e) {
				errorString += "Unable to perform remote data transfer: " + e + System.lineSeparator();
			}
		}

		if (errorString.length() == 0) {
			return new StatusBasic(EStatus.OKAY);
		}
		else {
			_logger.warn("remoteDataFlow failed: " + errorString);
			return new StatusBasic(EStatus.ERROR, errorString);
		}
	}
}
