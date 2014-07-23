package de.tum.in.i22.uc.gui;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tum.in.i22.uc.cm.datatypes.interfaces.IEvent;
import de.tum.in.i22.uc.cm.factories.IMessageFactory;
import de.tum.in.i22.uc.cm.factories.MessageFactoryCreator;
import de.tum.in.i22.uc.thrift.ThriftConverter;
import de.tum.in.i22.uc.thrift.types.TEvent;
import de.tum.in.i22.uc.thrift.types.TPep2Pdp;
import de.tum.in.i22.uc.thrift.types.TResponse;
import de.tum.in.i22.uc.thrift.types.TStatus;
import de.tum.in.i22.uc.thrift.types.TobiasEvent;
import de.tum.in.i22.uc.thrift.types.TobiasResponse;
import de.tum.in.i22.uc.thrift.types.TobiasStatusType;

public class RecordingServerImpl implements TPep2Pdp.Iface {

	private static LinkedList<IEvent> ll = new LinkedList<IEvent>();
	private IMessageFactory mf = MessageFactoryCreator.createMessageFactory();
	private static Logger log = LoggerFactory.getLogger(RecordingServerImpl.class);
	
	public RecordingServerImpl() {
		ll.clear();
	}
	
	@Override
	public void notifyEventAsync(TEvent pepEvent) throws TException {
		ll.add(ThriftConverter.fromThrift(pepEvent));
	}

	@Override
	public TResponse notifyEventSync(TEvent pepEvent) throws TException {
		ll.add(ThriftConverter.fromThrift(pepEvent));
		return new TResponse(TStatus.ALLOW);
	}

	@Override
	public void processEventAsync(TobiasEvent e, String senderID)
			throws TException {
		ll.add(mf.createActualEvent(e.getName(), e.getParameters()));
	}

	@Override
	public TobiasResponse processEventSync(TobiasEvent e, String senderID)
			throws TException {
		ll.add(mf.createDesiredEvent(e.getName(), e.getParameters()));
		return new TobiasResponse(TobiasStatusType.ALLOW);
	}

	public LinkedList<IEvent> getTrace() {
		return ll;
	}


}
