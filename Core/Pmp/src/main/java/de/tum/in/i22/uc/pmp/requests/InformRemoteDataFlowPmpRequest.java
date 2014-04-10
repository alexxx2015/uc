package de.tum.in.i22.uc.pmp.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PmpProcessor;

public class InformRemoteDataFlowPmpRequest extends PmpRequest<IStatus> {
	private final Location _srcLocation;
	private final Location _dstLocation;
	private final Set<IData> _dataflow;

	public InformRemoteDataFlowPmpRequest(Location srcLocation, Location dstLocation, Set<IData> dataflow) {
		_srcLocation = srcLocation;
		_dstLocation = dstLocation;
		_dataflow = dataflow;
	}

	@Override
	public IStatus process(PmpProcessor processor) {
		return processor.informRemoteDataFlow(_srcLocation, _dstLocation, _dataflow);
	}
}
