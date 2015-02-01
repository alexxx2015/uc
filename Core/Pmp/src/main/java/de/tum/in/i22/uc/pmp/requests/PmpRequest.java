package de.tum.in.i22.uc.pmp.requests;

import de.tum.in.i22.uc.cm.processing.ERequestType;
import de.tum.in.i22.uc.cm.processing.PmpProcessor;
import de.tum.in.i22.uc.cm.processing.Request;

public abstract class PmpRequest<R> extends Request<R,PmpProcessor>  {
	PmpRequest() {
		super(ERequestType.PMP_REQUEST);
	}
}
