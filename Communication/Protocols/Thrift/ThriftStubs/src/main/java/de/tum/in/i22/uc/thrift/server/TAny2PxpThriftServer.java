package de.tum.in.i22.uc.thrift.server;

import java.util.List;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.thrift.types.TAny2Pxp;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TStatus;

/**
 * Use {@link ThriftServerFactory} to create an instance.
 *
 * @author Florian Kelbert & ?
 *
 */
class TAny2PxpThriftServer extends ThriftServerHandler implements TAny2Pxp.Iface {
	private static Logger _logger = LoggerFactory.getLogger(TAny2PxpThriftServer.class);

	@Override
	public void executeAsync(List<TEvent> eventList) throws TException {
		// This method does nothing more than its synchronous counterpart.
		// The only difference is that the invoker is not busy while waiting for a reply.
		executeSync(eventList);
	}

	@Override
	public TStatus executeSync(List<TEvent> eventList) throws TException {
		_logger.info("PXP execute method invoked");

		//TODO: find a way to handle the return status for the execution of more than one event.
		// with this code, only the return TStatus of the last event executed is returned

		TStatus res=TStatus.OKAY;
		for (TEvent te : eventList) {
			if (te != null && te.getName() != null) {
				switch (te.getName()) {
				case "delmr":
					res=delmr(te.getParameters());
					break;
				case "myfunc1":
					res=myfunc1(te.getParameters());
					break;
				case "myfunc2":
					res=myfunc2(te.getParameters());
					break;
				default:
				}
			}
		}
		return res;
	}

	private TStatus delmr(Map<String,String> par){
		/**
		 * exmaple implementation
		 */
		_logger.info("Method delmr invoked - please add an implementation");
		return TStatus.OKAY;
	}

	private TStatus myfunc1(Map<String,String> par){
		/**
		 * ADD FUNCTIONALITY 1 HERE
		 */
		_logger.info("Method myfunc1 invoked - please add an implementation");
		return TStatus.OKAY;
	}


	private TStatus myfunc2(Map<String,String> par){
		/**
		 * ADD FUNCTIONALITY 2 HERE
		 */
		_logger.info("Method myfunc2 invoked - please add an implementation");
		return TStatus.OKAY;
	}


}
