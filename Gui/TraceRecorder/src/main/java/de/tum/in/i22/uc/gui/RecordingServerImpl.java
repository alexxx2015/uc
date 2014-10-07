package de.tum.in.i22.uc.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.thrift.TException;
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
		log.debug("Async notified event "+ pepEvent);
		ll.add(ThriftConverter.fromThrift(pepEvent));
	}

	@Override
	public TResponse notifyEventSync(TEvent pepEvent) throws TException {
		log.debug("Sync notified event "+ pepEvent);
		ll.add(ThriftConverter.fromThrift(pepEvent));
		TResponse tr =new TResponse(TStatus.ALLOW);
		tr.setModifiedEvent(new TEvent("fakeModifiedEvent",new HashMap<String,String>(),0));
		return tr;
	}

	@Override
	public void processEventAsync(TobiasEvent e, String senderID)
			throws TException {
		Map<String,String> map = new HashMap<String,String>(e.getParameters());
		log.debug("AsyncT notified event "+ e);
		ll.add(mf.createActualEvent(e.getName(), map));
	}

	@Override
	public TobiasResponse processEventSync(TobiasEvent e, String senderID)
			throws TException {
		Map<String,String> map = new HashMap<String,String>(e.getParameters());
		log.debug("SyncT notified event "+ e);
		ll.add(mf.createDesiredEvent(e.getName(), map));
		return new TobiasResponse(TobiasStatusType.ALLOW);
	}

	public LinkedList<IEvent> getTrace() {
		return ll;
	}


}
