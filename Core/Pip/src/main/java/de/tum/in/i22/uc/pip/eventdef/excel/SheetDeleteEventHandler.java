package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SheetDeleteEventHandler extends ExcelEvents {

	public SheetDeleteEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String workbookName = "";
		String worksheetName = "";
		try {
			workbookName = getParameterValue("workbookName");
			worksheetName = getParameterValue("worksheetName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((workbookName == null) || (workbookName.equals("")) ||
			(worksheetName == null) || (worksheetName.equals(""))) {
			_logger.debug("no thing to erase");
			return _messageFactory.createStatus(EStatus.ERROR_EVENT_PARAMETER_MISSING);
		}

		Collection<CellName> allCells = _informationFlowModel.getAllNames(CellName.class);

		for (CellName c: allCells) {
			if (c.getWorkbook().equals(workbookName) && c.getWorksheet().equals(worksheetName))
				_informationFlowModel.remove(_informationFlowModel.getContainer(c));
		}
	
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
