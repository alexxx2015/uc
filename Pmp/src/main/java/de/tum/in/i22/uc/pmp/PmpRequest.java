package de.tum.in.i22.uc.pmp;

import de.tum.in.i22.uc.cm.datatypes.IMechanism;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class PmpRequest<R> extends Request<R,PmpProcessor>  {
	private IMechanism _mechanism;
	private String _stringParameter;

	private PmpRequest(IMechanism mechanism, String stringParam) {
		_mechanism = mechanism;
		_stringParameter = stringParam;
	}

	public PmpRequest(IMechanism mechanism) {
		this(mechanism, null);
	}

	public PmpRequest(String stringParam) {
		this(null, stringParam);
	}

	public IMechanism getMechanism() {
		return _mechanism;
	}

	public String getStringParameter() {
		return _stringParameter;
	}

	@Override
	public String toString() {
		return com.google.common.base.Objects.toStringHelper(this)
				.add("_mechanism", _mechanism)
				.add("_stringParam", _stringParameter)
				.toString();
	}
//
//	public enum EPmpRequestType {
//		DEPLOY_MECHANISM,
//		EXPORT_MECHANISM,
//		REVOKE_MECHANISM;
//	}
}
