package de.tum.in.i22.uc.cm.pip;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.distribution.Location;

/**
 * Remembers which remote data flows have occurred.
 * The source {@link Location} of the data flow is fixed,
 * but there might be many destination {@link Location}s
 * to which different data has flown to different containers.
 *
 * @author Florian Kelbert
 *
 */
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

	/**
	 * Adds the information that data has flown from this object's
	 * source {@link Location} to the specified destination {@link Location}.
	 *
	 * @param dstLocation the location to which the data has flown
	 * @param contName the name of the container into which the data has flown
	 * @param data the data that has flown
	 */
	public void addFlow(Location dstLocation, IName contName, Set<IData> data) {
		if (_dataflow == null) {
			_dataflow = new HashMap<>();
		}

		Map<IName,Set<IData>> map = _dataflow.get(dstLocation);
		if (map == null) {
			_dataflow.put(dstLocation, map = new HashMap<>());
		}

		Set<IData> flow = map.get(contName);
		if (flow == null) {
			map.put(contName, flow = new HashSet<>());
		}
		flow.addAll(data);
	}

	public Map<Location, Map<IName,Set<IData>>> getFlows() {
		// TODO: inner elements can still be changed by caller.
		return Collections.unmodifiableMap(_dataflow);
	}

	/**
	 * Whether data has flown from this object's source {@link Location}.
	 * @return true if there is at least one destination {@link Location}
	 * 			to which data has flown; false otherwise.
	 */
	public boolean isEmpty() {
		return _dataflow == null || _dataflow.size() == 0;
	}


	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_srcLocation", _srcLocation)
				.add("_dataflow", _dataflow)
				.toString();
	}
}
