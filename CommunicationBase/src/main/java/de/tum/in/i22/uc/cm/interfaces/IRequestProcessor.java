package de.tum.in.i22.uc.cm.interfaces;

import de.tum.in.i22.uc.cm.requests.Request;

public interface IRequestProcessor<T extends Request> {
	public Object process(T request);
}
