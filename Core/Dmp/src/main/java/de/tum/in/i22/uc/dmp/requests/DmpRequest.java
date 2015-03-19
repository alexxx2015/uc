package de.tum.in.i22.uc.dmp.requests;

import de.tum.in.i22.uc.cm.processing.DmpProcessor;
import de.tum.in.i22.uc.cm.processing.ERequestType;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class DmpRequest<ResponseType> extends Request<ResponseType,DmpProcessor> {
	DmpRequest() {
		super(ERequestType.DISTR_REQUEST);
	}
}
