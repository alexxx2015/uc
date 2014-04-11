package de.tum.in.i22.uc.pip.requests;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.distribution.Location;
import de.tum.in.i22.uc.cm.server.PipProcessor;

public class WhoHasDataPipRequest extends PipRequest<Set<Location>> {
	private final Set<IData> _data;
	private final int _recursionDepth;

	public WhoHasDataPipRequest(Set<IData> data, int recursionDepth) {
		_data = data;
		_recursionDepth = recursionDepth;
	}

	@Override
	public Set<Location> process(PipProcessor processor) {
		return processor.whoHasData(_data, _recursionDepth);
	}
}
