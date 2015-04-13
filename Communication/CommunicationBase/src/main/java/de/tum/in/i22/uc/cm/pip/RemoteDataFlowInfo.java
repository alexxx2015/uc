package de.tum.in.i22.uc.cm.pip;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.linux.SocketContainer;

/**
 * Remembers which remote data flows have occurred.
 *
 * @author Florian Kelbert
 *
 */
public class RemoteDataFlowInfo {
	private static Logger _logger = LoggerFactory.getLogger(RemoteDataFlowInfo.class);

	/**
	 * Maps the data flow that has occurred.
	 */
	private final Map<SocketContainer,Set<IData>> _dataflow;

	public RemoteDataFlowInfo() {
		_dataflow = new HashMap<>();
	}

	/**
	 *
	 * @param dstCont
	 * @param data
	 */
	public void addFlow(SocketContainer dstCont, Set<IData> data) {
		_logger.info("addFlow: {}, {}.", dstCont, data);

		Set<IData> oldData = _dataflow.get(dstCont);
		if (oldData == null) {
			oldData = new HashSet<>();
			_dataflow.put(dstCont, oldData);
		}
		oldData.addAll(data);
	}

	public Map<SocketContainer,Set<IData>> getFlows() {
		// TODO: inner elements can still be changed by caller.
		return Collections.unmodifiableMap(_dataflow);
	}

	/**
	 * Whether data has flown from this object's source {@link SocketContainer}.
	 * @return true if there is at least one destination {@link SocketContainer}
	 * 			to which data has flown; false otherwise.
	 */
	public boolean isEmpty() {
		return _dataflow.isEmpty();
	}


	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("_dataflow", _dataflow)
				.toString();
	}
}
