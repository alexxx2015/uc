package de.tum.in.i22.pip2pip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import de.tum.in.i22.uc.cm.basic.EventBasic;
import de.tum.in.i22.uc.cm.basic.StatusBasic;
import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IName;
import de.tum.in.i22.uc.cm.datatypes.IStatus;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpEvent;
import de.tum.in.i22.uc.cm.gpb.PdpProtos.GpStatus;
import de.tum.in.i22.uc.cm.interfaces.IPip2Pip;
import de.tum.in.i22.uc.cm.methods.EPipRequestMethod;
import de.tum.in.i22.uc.cm.out.Connection;
import de.tum.in.i22.uc.cm.out.Connector;

public class Pip2PipImp extends Connection implements IPip2Pip {

	public Pip2PipImp(Connector connector) {
		super(connector);
	}

	@Override
	public boolean hasAllData(Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyData(Collection<IData> data) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAllContainers(Collection<IContainer> container) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean hasAnyContainer(Collection<IContainer> container) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public IStatus notifyDataTransfer(IName containerName, Collection<IData> data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
 		_logger.debug("notifyEventToPip method entered");

		_logger.trace("Create Google Protocol Buffer event instance");
		GpEvent gpEvent = EventBasic.createGpbEvent(event);
		try {
			OutputStream out = getOutputStream();
			out.write(EPipRequestMethod.NOTIFY_ACTUAL_EVENT.getValue());

			gpEvent.writeDelimitedTo(out);
			out.flush();
			_logger.trace("Event written to OutputStream");

			_logger.trace("Wait for GpStatus");
			GpStatus gpStatus = GpStatus.parseDelimitedFrom(getInputStream());

			return new StatusBasic(gpStatus);
		} catch (IOException ex) {
			_logger.error("Failed to notify event.", ex);
			//TODO better throw custom unchecked exception than return null
			return null;
		}
	}

}
