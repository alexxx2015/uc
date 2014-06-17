package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class UpdateCellContentEventHandler extends ExcelEvents {

	public UpdateCellContentEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String target = "";
		String affectedRange="";
		try {
			target = getParameterValue("Target");
			affectedRange=getParameterValue("affectedRange");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((target == null) || (target.equals(""))) {
			_logger.debug("nothign to copy. leaving system clipboard intact.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		Set<CellName> affectedSet = getSetOfCells(affectedRange);
		CellName targetCellName = new CellName(target);
		IContainer targetContainer = _informationFlowModel.getContainer(targetCellName);
		
		if (targetContainer==null){
			targetContainer = new ContainerBasic();
			_informationFlowModel.addName(targetCellName, targetContainer, true);
		}
			
		for (CellName c: affectedSet) {
			IContainer src= _informationFlowModel.getContainer(c);
			if (src ==null){
				src = new ContainerBasic();
				_informationFlowModel.addName(c, src, true);
			}
			_informationFlowModel.addAlias(src, targetContainer);
		}
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
