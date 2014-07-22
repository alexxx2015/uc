package de.tum.in.i22.uc.pip.extensions.distribution;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic;
import de.tum.in.i22.uc.cm.pip.RemoteDataFlowInfo;

public class DistributedPipStatus extends StatusBasic {
	private final RemoteDataFlowInfo _dataflow;

	private DistributedPipStatus(EStatus eStatus, RemoteDataFlowInfo dataflow) {
		super(eStatus);
		_dataflow = dataflow;
	}

	public static DistributedPipStatus createRemoteDataFlowStatus(RemoteDataFlowInfo dataflow) {
		return new DistributedPipStatus(EStatus.REMOTE_DATA_FLOW_HAPPENED, dataflow);
	}

	public RemoteDataFlowInfo getDataflow() {
		return _dataflow;
	}
}
