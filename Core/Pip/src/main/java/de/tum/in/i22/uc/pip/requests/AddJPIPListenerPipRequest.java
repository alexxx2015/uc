package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.cm.processing.PipProcessor;

public class AddJPIPListenerPipRequest extends PipRequest<IStatus> {

	private final String _ip;
	private final int _port;
	private final String _id;
	private final String _filter;

	public AddJPIPListenerPipRequest(String ip, int port, String id, String filter) {
		_ip=ip;
		_port=port;
		_id=id;
		_filter=filter;
	}

	@Override
	public IStatus process(PipProcessor processor) {
		return processor.addJPIPListener(_ip, _port, _id, _filter);
	}
}
