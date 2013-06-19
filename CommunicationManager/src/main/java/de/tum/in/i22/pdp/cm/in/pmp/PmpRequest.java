package de.tum.in.i22.pdp.cm.in.pmp;

import de.tum.in.i22.pdp.cm.in.EPmp2PdpMethod;
import de.tum.in.i22.pdp.datatypes.IMechanism;

public class PmpRequest {
	private EPmp2PdpMethod _method;
	private IMechanism _mechanism;
	private String _stringParameter;
	
	private PmpRequest(EPmp2PdpMethod method, IMechanism mechanism, String stringParam) {
		if (method == EPmp2PdpMethod.DEPLOY_MECHANISM) {
			_mechanism = mechanism;
		} else if (method == EPmp2PdpMethod.EXPORT_MECHANISM
				|| method == EPmp2PdpMethod.REVOKE_MECHANISM){
			_stringParameter = stringParam;
		} else {
			throw new RuntimeException("Method parameter combination in PmpRequest not possible!");
		}
	}
	
	public PmpRequest(EPmp2PdpMethod method, IMechanism mechanism) {
		this(method, mechanism, null);
	}
	
	public PmpRequest(EPmp2PdpMethod method, String stringParam) {
		this(method, null, stringParam);
	}
	
	public EPmp2PdpMethod getMethod() {
		return _method;
	}
	
	public IMechanism getMechanism() {
		return _mechanism;
	}
	
	public String getStringParameter() {
		return _stringParameter;
	}
}
