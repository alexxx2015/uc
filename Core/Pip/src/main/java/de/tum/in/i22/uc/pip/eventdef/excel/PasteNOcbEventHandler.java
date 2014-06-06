package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class PasteNOcbEventHandler extends ExcelEvents {

	public PasteNOcbEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String target = "";
		int pos = -1;
		try {
			target = getParameterValue("Target");
			pos= Integer.valueOf(getParameterValue("position"));
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		
		IContainer posNOfOcb=getOCB(pos);
		
		if ((target == null) || (target.equals(""))||(pos<0)||(posNOfOcb==null)) {
			_logger.debug("no place to paste or wrong position from office clipboard");
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
		}

		Set<CellName> targetSet = getSetOfCells(target);
		
		for (CellName c: targetSet) {
			IContainer dst= _informationFlowModel.getContainer(c);
			_informationFlowModel.copyData(posNOfOcb,dst);
		}
	
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
