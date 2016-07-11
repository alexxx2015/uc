package de.tum.in.i22.uc.thrift.server;

import java.nio.ByteBuffer;

import org.apache.thrift.TException;

import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;
import de.tum.in.i22.uc.thrift.types.TAny2Prp;

public class TAny2PrpThriftServer extends ThriftServerHandler implements
		TAny2Prp.Iface {

	private final IAny2Prp _handler;

	TAny2PrpThriftServer(IAny2Prp handler) {
		this._handler = handler;
	}

	@Override
	public void deployReleaseMechanism(ByteBuffer mechanism) throws TException {
		// TODO Auto-generated method stub
		_handler.deployReleaseMechanism(mechanism);
	}

	@Override
	public ByteBuffer getMechanism(String mechanismName) throws TException {
		// TODO Auto-generated method stub
		return _handler.getMechanism(mechanismName);
	}

}
