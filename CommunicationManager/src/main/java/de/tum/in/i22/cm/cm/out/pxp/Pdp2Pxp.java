package de.tum.in.i22.cm.cm.out.pxp;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import de.tum.in.i22.cm.pdp.internal.ExecuteAction;
import de.tum.in.i22.cm.pdp.internal.Param;
import de.tum.in.i22.uc.cm.datatypes.IPxpSpec;

public class Pdp2Pxp implements IPdp2Pxp {

	@Override
	public void sendExecAction2Pxp(IPxpSpec pxpSpec, ExecuteAction execAction) {
		// TODO Auto-generated method stub
		try {
			TTransport transport = new TSocket(pxpSpec.getIp(), pxpSpec.getPort());
			transport.open();
		
			TProtocol protocol = new TBinaryProtocol(transport);
			JavaPxp.Client client = new JavaPxp.Client(protocol);
			
			Param<?> olderThan = execAction.getParameterForName("OLDERTHAN");
			Param<?> unit = execAction.getParameterForName("UNIT"); 
			
			
			client.delmr("delete this shit "+unit.getValue(), Short.valueOf((String)olderThan.getValue()));
			transport.close();
		} catch (NumberFormatException | TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
