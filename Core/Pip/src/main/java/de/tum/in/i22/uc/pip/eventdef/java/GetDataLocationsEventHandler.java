package de.tum.in.i22.uc.pip.eventdef.java;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IData;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;
import de.tum.in.i22.uc.pip.eventdef.java.chopnode.AssignChopNodeLabel;

public class GetDataLocationsEventHandler extends JavaEventHandler {

    @Override
    protected IStatus update() {
	String pid = null;
	String sourceId = null;
	long minTimeStamp = 0;

	try {
	    pid = getParameterValue("processId");
	    sourceId = getParameterValue("sourceId");
	    minTimeStamp = Long.valueOf(getParameterValue("minTimeStamp"));
	} catch (ParameterNotFoundException e) {
	    _logger.error(e.getMessage());
	    return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
	}

	// Filter all containers containing data with specified sourceId and
	// older than minTimeStamp
	Set<IContainer> containers = new HashSet<>();
	for (IContainer container : _informationFlowModel.getAllContainers()) {
	    for (IData data : _informationFlowModel.getData(container)) {
		String[] comps = data.getId().split("\\" + DLM);
		String dataSourceId = comps[0];
		long dataTimeStamp = Long.valueOf(comps[1]);
		if (sourceId.equals(dataSourceId) && dataTimeStamp < minTimeStamp) {
		    containers.add(container);
		    continue;
		}
	    }
	}

	for (IContainer container : containers) {
	    Collection<IName> names = _informationFlowModel.getAllNames(container);
	    for (IName name : names) {
		String nameString = name.getName();
		if (nameString.startsWith(pid)) {
		    
		}
	    }
	}

	return _messageFactory.createStatus(EStatus.ALLOW, "foo");
    }

}
