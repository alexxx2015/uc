package de.tum.i22.monitor.connector;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import de.tum.in.i22.pep2pdp.IPep2PdpFast;
import de.tum.in.i22.pep2pdp.Pep2PdpFastImp;
import de.tum.in.i22.uc.cm.IMessageFactory;
import de.tum.in.i22.uc.cm.MessageFactoryCreator;
import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.ResponseBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.EStatus;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IResponse;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus.GpEStatus;

public class ThriftServerHandler implements PDPThriftConnector.Iface {
	
	private static IPep2PdpFast pdpProxyOne;
	
	public ThriftServerHandler(int pepPort) {
		// TODO Auto-generated constructor stub
		// we should start it on this port
		pdpProxyOne = new Pep2PdpFastImp("localhost",
				pepPort);
		try {
			pdpProxyOne.connect();
		} catch (Exception e) {
			_logger.error(e);
		}

	
	}
	
	private static final Logger _logger = Logger
			.getLogger(ThriftServerHandler.class);

	@Override
	public void processEventAsync(Event e) throws TException {
		// TODO Auto-generated method stub
		_logger.debug("processEventAsync invoked, but not supported");
		//Not needed
	}

	@Override
	public Response processEventSync(Event e) throws TException {
		IEvent ev=new EventBasic(e.name,e.parameters,false);
		_logger.trace("received event "+e.name);
		IResponse ir= pdpProxyOne.notifyEvent(ev);
		
//		IStatus auth=new StatusBasic(EStatus.ALLOW,"");
//		IResponse re= new ResponseBasic(auth,null,null);
		switch (ir.getAuthorizationAction().getEStatus()){
		case INHIBIT:
			return new Response(StatusType.INHIBIT);

		case ALLOW:
			return new Response(StatusType.ALLOW);

		case MODIFY:
			//TODO: Add modification action cause it is not supported on the PEP side yet
			return new Response(StatusType.MODIFY);
		default: 
			Response r=new Response(StatusType.ERROR);;
			r.setComment("Error. Answer is " + ir);
			return r;		
		}
	}
	
}
