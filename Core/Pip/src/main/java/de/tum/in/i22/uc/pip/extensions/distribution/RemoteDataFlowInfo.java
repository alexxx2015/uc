package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.distribution.Location;

public class RemoteDataFlowInfo {
	/**
	 * the location from which the data has flown
	 */
	private final Location _srcLocation;

	/**
	 * Maps the data flow that has occurred:
	 * - to which location
	 *   - into which container (names) at this location
	 *     - which data
	 */
	private Map<Location,Map<IName,Set<IData>>> _dataflow;

	public RemoteDataFlowInfo(Location srcLocation) {
		_srcLocation = srcLocation;
	}

	public Location getSrcLocation() {
		return _srcLocation;
	}

	public void addFlow(Location dstLocation, IName contName, Set<IData> data) {
		if (_dataflow == null) {
			_dataflow = new HashMap<>();
		}

		Map<IName,Set<IData>> map = _dataflow.get(dstLocation);
		if (map == null) {
			map = new HashMap<>();
			_dataflow.put(dstLocation, map);
		}

		Set<IData> flow = map.get(contName);
		if (flow == null) {
			flow = new HashSet<>();
			map.put(contName, flow);
		}
		flow.addAll(data);
	}

	public Map<Location, Map<IName,Set<IData>>> getFlows() {
		// TODO: inner elements can still be changed by caller.
		return Collections.unmodifiableMap(_dataflow);
	}

	public boolean isEmpty() {
		return _dataflow == null || _dataflow.size() == 0;
	}
}
