package de.tum.in.i22.uc.cm.distribution.requests;

import de.tum.in.i22.uc.cm.distribution.IDistributionManagerPublic;
import de.tum.in.i22.uc.cm.processing.ERequestType;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class DistributionRequest<ResponseType> extends Request<ResponseType,IDistributionManagerPublic> {
	DistributionRequest() {
		super(ERequestType.DISTR_REQUEST);
	}
}
