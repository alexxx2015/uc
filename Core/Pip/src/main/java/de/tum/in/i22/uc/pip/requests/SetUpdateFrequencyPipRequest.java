package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class SetUpdateFrequencyPipRequest extends PipRequest<IStatus> {

	private final int _msec;
	private final String _id;

	public SetUpdateFrequencyPipRequest(int msec, String id) {
		_msec=msec;
		_id=id;
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.setUpdateFrequency(_msec, _id);
	}
}
