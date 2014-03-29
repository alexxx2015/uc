package de.tum.in.i22.uc.cm.requests;

import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;

public class PdpRequest<R> extends Request<R> {
	private final EPdpRequestType _type;
	private IPxpSpec _pxp;
	private IEvent _event;

	public PdpRequest(EPdpRequestType type, Class<R> responseClass) {
		super(responseClass);
		_type = type;
	}

	public PdpRequest(EPdpRequestType type, IPxpSpec pxp, Class<R> responseClass) {
		this(type, responseClass);
		_pxp = pxp;
	}

	public PdpRequest(EPdpRequestType type, IEvent event, Class<R> responseClass) {
		this(type, responseClass);
		_event = event;
	}

	public EPdpRequestType getType() {
		return _type;
	}

	public IPxpSpec getPxp() {
		return _pxp;
	}

	public IEvent getEvent() {
		return _event;
	}

	public enum EPdpRequestType {
		NOTIFY_EVENT,
		REGISTER_PXP,
		DEPLOY_MECHANISM,
		EXPORT_MECHANISM,
		REVOKE_MECHANISM,
		DEPLOY_POLICY,
		LIST_MECHANISMS;
	}

}
