package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class CloseWorkbookEventHandler extends ExcelEvents {

	public CloseWorkbookEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String workbookName = "";

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

		try {

			workbookName = getParameterValue("srcWorkbookName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((allCells == null) || (workbookName == null)
				|| (workbookName.equals("")))
			throw new RuntimeException("impossible to close the workbook");

		/*
		 * for (CellName cell : allCells) { if
		 * (cell.getWorkbook().equals(srcWorkbookName) &&
		 * cell.getWorksheet().equals(sheetName )) { throw new RuntimeException(
		 * "impossible to move the sheet: destination already contains exists");
		 * 
		 * }
		 * 
		 * }
		 */
		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(workbookName)) {

				_informationFlowModel.removeName(cell, true);
			}

		}

		return _messageFactory.createStatus(EStatus.OKAY);

	}

}
