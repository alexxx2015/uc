package de.tum.in.i22.uc.cm.thrift;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.i22.in.uc.thrift.types.TAny2Pmp;
import de.tum.in.i22.uc.thrift.server.ThriftServerHandler;


public class TAny2PmpThriftProcessor extends ThriftServerHandler implements TAny2Pmp.Iface {
	protected static Logger _logger = LoggerFactory.getLogger(TAny2PmpThriftProcessor.class);
}
