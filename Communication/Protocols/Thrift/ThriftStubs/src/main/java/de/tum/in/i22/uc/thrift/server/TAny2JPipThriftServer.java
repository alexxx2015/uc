package de.tum.in.i22.uc.thrift.server;

import java.util.List;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TPip2JPip;

/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Lovat 
 * 
 */
class TAny2JPipThriftServer extends ThriftServerHandler implements TPip2JPip.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2JPipThriftServer.class);

	@Override
	public void notifyAsync(List<TEvent> eventList) throws TException {
		_logger.debug("Hi, I'm a JPIP server");
	}

}
