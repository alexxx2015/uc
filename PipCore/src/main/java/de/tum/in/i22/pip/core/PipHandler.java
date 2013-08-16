package de.tum.in.i22.pip.core;

import java.util.List;

import de.tum.in.i22.uc.cm.datatypes.IContainer;
import de.tum.in.i22.uc.cm.datatypes.IData;
import de.tum.in.i22.uc.cm.datatypes.IEvent;
import de.tum.in.i22.uc.cm.datatypes.IStatus;

public class PipHandler implements IPdp2Pip {
	
	private PipSemantics _pipSemantics = null;
	private PipModel _pipModel = null;
	
	public PipHandler() {
		_pipSemantics = new PipSemantics();
		_pipModel = new PipModel();
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
		return _pipSemantics.processEvent(event, _pipModel);
	}

//	@Override
//	public int updatePIP(PDPEvent newEvent) {
//        if (pipSemantics == null | pipModel == null)
//        {
//        	System.out.println("PIP not yet initialized => call initializePIP() first!" + "[PIPLib]");
//            return -1;
//        }
//
//        return pipSemantics.processEvent(newEvent, pipModel);
//	}
}
