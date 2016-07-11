package de.tum.in.i22.uc.thrift.client;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.interfaces.IAny2Prp;
import de.tum.in.i22.uc.thrift.types.TAny2Pdp;
import de.tum.in.i22.uc.thrift.types.TAny2Pip;
import de.tum.in.i22.uc.thrift.types.TAny2Prp;

public class ThriftAny2PrpImpl implements IAny2Prp {
	
	protected static final Logger _logger = LoggerFactory.getLogger(ThriftAny2PrpImpl.class);

	private final TAny2Prp.Client _handle;

	public ThriftAny2PrpImpl(TAny2Prp.Client handle) {
		_handle = handle;
	}

	@Override
	public void deployReleaseMechanism(ByteBuffer mechanism) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ByteBuffer getMechanism(String mechanismName) {
		// TODO Auto-generated method stub
		return null;
	}

}
