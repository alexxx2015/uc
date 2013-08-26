package de.tum.in.i22.pip.core;

import java.util.List;

import org.apache.log4j.Logger;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipHandler implements IPdp2Pip {
	
	private static final Logger _logger = Logger.getLogger(PipHandler.class);
	
	private PipSemantics _pipSemantics = null;
	
	public PipHandler() {
		_pipSemantics = new PipSemantics();
	}

	@Override
	public Boolean evaluatePredicate(IEvent event, String predicate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IContainer> getContainerForData(IData data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<IData> getDataInContainer(IContainer container) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IStatus notifyActualEvent(IEvent event) {
		_logger.trace("Delegate notify actual event to PipSemantics");
		return _pipSemantics.processEvent(event);
	}
}
