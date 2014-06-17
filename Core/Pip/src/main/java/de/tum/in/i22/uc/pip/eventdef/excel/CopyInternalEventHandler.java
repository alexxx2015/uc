package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class CopyInternalEventHandler extends ExcelEvents {

	public CopyInternalEventHandler() {
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
			_logger.debug("nothign to copy. leaving system clipboard intact.");
			return _messageFactory.createStatus(EStatus.OKAY);
		}

		Set<CellName> targetSet = getSetOfCells(target);
		
		IContainer sysClip = _informationFlowModel.getContainer(new NameBasic(scbName));
		if (sysClip==null) {
			sysClip=new ContainerBasic();
			_informationFlowModel.addName(new NameBasic(scbName), sysClip, true);
		}
		_informationFlowModel.emptyContainer(sysClip);
		
		for (CellName c: targetSet) {
			IContainer src= _informationFlowModel.getContainer(c);
			_informationFlowModel.copyData(src,sysClip);
		}
		
		pushOCB();
		
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
