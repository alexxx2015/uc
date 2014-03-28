package de.tum.in.i22.uc.cm.processor;

import de.tum.in.i22.uc.cm.requests.Request;

/**
 *
 * @author Florian Kelbert
 *
 * @param <Req>
 */
interface Processor<Req extends Request> {
	public abstract Object process(Req request);
}
