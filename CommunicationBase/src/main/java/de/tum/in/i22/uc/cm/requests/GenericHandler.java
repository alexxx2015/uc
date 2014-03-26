package de.tum.in.i22.uc.cm.requests;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Florian Kelbert
 *
 * @param <Req>
 */
public abstract class GenericHandler<Req extends Request> {
	protected static final Logger _logger = LoggerFactory.getLogger(GenericHandler.class);
	public abstract Object process(Req request);
}
