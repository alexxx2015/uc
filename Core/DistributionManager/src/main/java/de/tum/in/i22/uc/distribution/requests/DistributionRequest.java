package de.tum.in.i22.uc.distribution.requests;

import de.tum.in.i22.uc.cm.distribution.IDistributionManagerExternal;
import de.tum.in.i22.uc.cm.processing.ERequestType;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class DistributionRequest<ResponseType> extends Request<ResponseType,IDistributionManagerExternal> {
	DistributionRequest() {
		super(ERequestType.DISTR_REQUEST);
	}
}
