package de.tum.in.i22.uc.pmp.requests;

import java.util.Map;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PmpProcessor;

public class InformRemoteDataFlowPmpRequest extends PmpRequest<IStatus> {
	private final Map<Location, Map<IName,Set<IData>>> _dataflow;

	public InformRemoteDataFlowPmpRequest(Map<Location,Map<IName, Set<IData>>> dataflow) {
		_dataflow = dataflow;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.informRemoteDataFlow(_dataflow);
	}
}
