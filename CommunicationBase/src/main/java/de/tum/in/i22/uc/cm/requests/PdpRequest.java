package de.tum.in.i22.uc.cm.requests;

import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;

public class PdpRequest extends Request {
	private final EPdpRequestType _type;
	private IPxpSpec _pxp;

	public PdpRequest(EPdpRequestType type) {
		_type = type;
	}

	public PdpRequest(EPdpRequestType type, IPxpSpec pxp) {
		this(type);
		_pxp = pxp;
	}

	public EPdpRequestType getType() {
		return _type;
	}

	public IPxpSpec getPxp() {
		return _pxp;
	}
}
