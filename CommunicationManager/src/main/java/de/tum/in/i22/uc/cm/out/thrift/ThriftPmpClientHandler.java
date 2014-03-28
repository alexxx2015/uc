package de.tum.in.i22.uc.cm.out.thrift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.cm.thrift.TAny2Pmp;


/**
 *
 * @author Florian Kelbert
 *
 */
public class ThriftPmpClientHandler extends PmpClientHandler<TAny2Pmp.Client> {
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftPmpClientHandler.class);

	public ThriftPmpClientHandler(String address, int port) {
		super(new ThriftConnector<>(address, port, TAny2Pmp.Client.class));
	}
}
