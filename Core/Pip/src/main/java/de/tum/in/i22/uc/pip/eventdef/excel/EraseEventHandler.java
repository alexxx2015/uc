package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class EraseEventHandler extends ExcelEvents {

	public EraseEventHandler() {
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
		if ((target == null) || (target.equals(""))) {
			_logger.debug("no thing to erase");
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
		}

		Set<CellName> targetSet = getSetOfCells(target);
		
		for (CellName c: targetSet) {
			_informationFlowModel.remove(_informationFlowModel.getContainer(c));
		}
	
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
