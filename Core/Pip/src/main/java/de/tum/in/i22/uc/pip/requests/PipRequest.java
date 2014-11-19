package de.tum.in.i22.uc.pip.requests;

import de.tum.in.i22.uc.cm.processing.ERequestType;
import de.tum.in.i22.uc.cm.processing.PipProcessor;
import de.tum.in.i22.uc.cm.processing.Request;

/**
 * Class for handling remote PIP requests.
 * @author Florian Kelbert
 *
 */
public abstract class PipRequest<R> extends Request<R, PipProcessor> {
	PipRequest() {
		super(ERequestType.PIP_REQUEST);
	}
}
