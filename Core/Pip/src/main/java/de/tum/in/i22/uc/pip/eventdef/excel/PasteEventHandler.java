package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class PasteEventHandler extends ExcelEvents {

	public PasteEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String target = "";
		
		try {
			target = getParameterValue("target");
			

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((target == null) || (target.equals(""))) {
			_logger.debug("no place to paste");
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
		}

		Set<CellName> targetSet = getSetOfCells(target);
		
		IContainer headOfOCB = headOCB();
		
		for (CellName c: targetSet) {
			IContainer dst= _informationFlowModel.getContainer(c);
			if (dst==null) {
				dst=new ContainerBasic();
				_informationFlowModel.addName(c, dst);
			}
			_informationFlowModel.copyData(headOfOCB,dst);
		}
	
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
