package de.tum.in.i22.uc.cm.distribution.pip;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.distribution.Location;

public class PipStatus extends StatusBasic {
	private final Map<Location,Map<IName,Set<IData>>> _dataflow;

	private PipStatus(EStatus eStatus, Map<Location,Map<IName,Set<IData>>> dataflow) {
		super(eStatus);
		_dataflow = dataflow;
	}

	public static PipStatus createRemoteDataFlowStatus(Map<Location,Map<IName,Set<IData>>> dataflow) {
		return new PipStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED, dataflow);
	}

	public Map<Location,Map<IName,Set<IData>>> getDataflow() {
		// TODO?! Inner map and set are still modifiable.
		return Collections.unmodifiableMap(_dataflow);
	}
}
