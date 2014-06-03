package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class PrintEventHandler extends ExcelEvents {

	public PrintEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String target = "";		
		try {
			target = getParameterValue("Target");
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((target==null)||(target.equals(""))) throw new RuntimeException("impossible to print empty target");
		
		String[] coordinates = target.split(cs);
		
		for (String s : coordinates) _logger.debug("coordinates = "+s);
		
		if (coordinates.length!=4) throw new RuntimeException("bad coordinates ("+coordinates+"). aborting");	

		IContainer src=_informationFlowModel.getContainer(new NameBasic(target));
		IContainer dst=_informationFlowModel.getContainer(new NameBasic("printer"));
		
		_informationFlowModel.copyData(src,dst);

		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
