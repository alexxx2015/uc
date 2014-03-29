package de.tum.in.i22.uc.cm.requests;

import de.tum.in.i22.uc.cm.datatypes.IMechanism;

public class PmpRequest<R> extends Request<R>  {
	private final EPmpRequestType _requestType;
	private IMechanism _mechanism;
	private String _stringParameter;

	private PmpRequest(EPmpRequestType requestType, IMechanism mechanism, String stringParam) {
		_requestType = requestType;
		if (requestType == EPmpRequestType.DEPLOY_MECHANISM) {
			_mechanism = mechanism;
		} else if (requestType == EPmpRequestType.EXPORT_MECHANISM
				|| requestType == EPmpRequestType.REVOKE_MECHANISM){
			_stringParameter = stringParam;
		} else {
			throw new RuntimeException("Method parameter combination in PmpRequest not possible!");
		}
	}

	public PmpRequest(EPmpRequestType method, IMechanism mechanism) {
		this(method, mechanism, null);
	}

	public PmpRequest(EPmpRequestType method, String stringParam) {
		this(method, null, stringParam);
	}

	public EPmpRequestType getType() {
		return _requestType;
	}

	public IMechanism getMechanism() {
		return _mechanism;
	}

	public String getStringParameter() {
		return _stringParameter;
	}

	@Override
	public String toString() {
		return "PmpRequest [_method=" + _requestType + ", _mechanism=" + _mechanism
				+ ", _stringParameter=" + _stringParameter + "]";
	}

	public enum EPmpRequestType {
		DEPLOY_MECHANISM,
		EXPORT_MECHANISM,
		REVOKE_MECHANISM;
	}
}
