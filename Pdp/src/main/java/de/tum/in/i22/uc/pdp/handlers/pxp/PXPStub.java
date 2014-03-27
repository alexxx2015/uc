package de.tum.in.i22.uc.pdp.handlers.pxp;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;
import de.tum.in.i22.uc.cm.pdp.core.Event;
import de.tum.in.i22.uc.cm.pdp.core.IPdpExecuteAction;
import de.tum.in.i22.uc.cm.pdp.core.Param;
import de.tum.in.i22.uc.pdp.core.PolicyDecisionPoint;

public class PXPStub implements IPxp
{
  private static Logger log = LoggerFactory.getLogger(PXPStub.class);

  @Override
  public boolean execute(IPdpExecuteAction execAction, Event event)
  {
    log.info("[PXPStub] Executing {} with parameters: {}", execAction.getName(), execAction.getParams());

    String pxpId = execAction.getId();

    if(pxpId != null){
		if(PolicyDecisionPoint.pxpSpec.containsKey(pxpId)){
			IPxpSpec pxpSpec = PolicyDecisionPoint.pxpSpec.get(pxpId);


			try {
				TTransport transport = new TSocket(pxpSpec.getIp(), pxpSpec.getPort());
				transport.open();

				TProtocol protocol = new TBinaryProtocol(transport);
				JavaPxp.Client client = new JavaPxp.Client(protocol);

				Param<?> olderThan = execAction.getParameterForName("OLDERTHAN");
				Param<?> unit = execAction.getParameterForName("UNIT");


				client.delmr(unit.getValue().toString(), Short.valueOf((String)olderThan.getValue()));
				transport.close();
			} catch (NumberFormatException | TException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
    }

    return true;
  }



}
