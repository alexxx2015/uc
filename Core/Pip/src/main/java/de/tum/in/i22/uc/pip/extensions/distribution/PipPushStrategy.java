package de.tum.in.i22.uc.pip.extensions.distribution;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.distribution.EDistributionStrategy;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.distribution.Location.ELocation;
import de.tum.in.i22.uc.cm.distribution.client.Pip2PipClient;



public class PipPushStrategy extends PipDistributionStrategy {
	protected static final Logger _logger = LoggerFactory.getLogger(PipPushStrategy.class);

	private final Map<Location, Set<IName>> _hasContainers;

	public PipPushStrategy(EDistributionStrategy eStrategy) {
		super(eStrategy);
		_hasContainers = new HashMap<>();
	}

	@Override
	public boolean hasAllData(Location location, Set<IData> data) {
		return _ifModel.getData(location).containsAll(data);
	}

	@Override
	public boolean hasAnyData(Location location, Set<IData> data) {
		return Sets.intersection(_ifModel.getData(location), data).size() > 0;
	}

	@Override
	public boolean hasAllContainers(Location location, Set<IName> containers) {
		// TODO
		return false;
	}

	@Override
	public boolean hasAnyContainer(Location location, Set<IName> containers) {
		// TODO
		return false;
	}

	@Override
	public IStatus doRemoteEventUpdate(Location location, IEvent event) {
		_logger.debug("remoteEventUpdate(" + location + "," + event + ")");

		try {
			Pip2PipClient pipHandle = _connectionManager.obtain(_clientHandlerFactory.createPip2PipClient(location));

			pipHandle.update(event);

			_connectionManager.release(pipHandle);
		} catch (IOException e) {
			_logger.warn("remoteEventUpdate failed: " + e.getMessage());
			return new StatusBasic(EStatus.ERROR, e.getMessage());
		}

		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public IStatus remoteDataFlow(Location srcLocation, Location dstLocation, Map<IName, Set<IData>> dataflow) {

		Set<IData> allData = new HashSet<>();
		for (Set<IData> d : dataflow.values()) {
			allData.addAll(d);
		}


		/*
		 *  In any case we know that srcLocation and dstLocation
		 *  contain the specified data.
		 */

		IContainer srcCont = _ifModel.getContainer(srcLocation);
		if (srcCont == null) {
			_ifModel.addName(srcLocation, srcCont = new ContainerBasic());
		}
		_ifModel.addData(allData, srcCont);

		IContainer dstCont = _ifModel.getContainer(dstLocation);
		if (dstCont == null) {
			_ifModel.addName(dstLocation, dstCont = new ContainerBasic());
		}
		_ifModel.addData(allData, dstCont);



		if (dstLocation.getLocation() == ELocation.LOCAL) {
			// data was transferred to this local location.
			_logger.info("Data was transferred from [" + srcLocation + "] to [" + dstLocation + "]: " + dataflow);
		}
		else {
			// tell the remote site about data transfer

			_logger.info("Performing remote data flow transfer from [" + srcLocation + "] to [" + dstLocation + "]: " + dataflow);

			try {
				Pip2PipClient _pipHandle = _connectionManager.obtain(_clientHandlerFactory.createPip2PipClient(dstLocation));

				for (Entry<IName,Set<IData>> entry : dataflow.entrySet()) {
					_pipHandle.initialRepresentation(entry.getKey(), entry.getValue());
				}

				_connectionManager.release(_pipHandle);

			} catch (IOException e) {
				_logger.warn("Unable to perform remote data transfer: " + e);
				return new StatusBasic(EStatus.ERROR, "Unable to perform remote data transfer: " + e + System.lineSeparator());
			}
		}

		return new StatusBasic(EStatus.OKAY);
	}

	@Override
	public Set<Location> whoHasData(Set<IData> data, int recursionDepth) {
//		_ifModel.getContainers(data); TODO
		return null;
	}
}
