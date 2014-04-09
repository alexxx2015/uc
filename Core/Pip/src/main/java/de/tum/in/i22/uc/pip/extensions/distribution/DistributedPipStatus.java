package de.tum.in.i22.uc.pip.extensions.distribution;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.distribution.Location;

public class DistributedPipStatus extends StatusBasic {
	private final Map<Location,Map<IName,Set<IData>>> _dataflow;

	private DistributedPipStatus(EStatus eStatus, Map<Location,Map<IName,Set<IData>>> dataflow) {
		super(eStatus);
		_dataflow = dataflow;
	}

	public static DistributedPipStatus createRemoteDataFlowStatus(Map<Location,Map<IName,Set<IData>>> dataflow) {
		return new DistributedPipStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED, dataflow);
	}

	public Map<Location,Map<IName,Set<IData>>> getDataflow() {
		// TODO: Inner map and set are still modifiable.
		return Collections.unmodifiableMap(_dataflow);
	}
}
