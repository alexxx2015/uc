package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SheetRenameEventHandler extends ExcelEvents {

	public SheetRenameEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String workbookName = "";
		String oldSheetName = "";
		String newSheetName = "";

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

		try {

			workbookName = getParameterValue("workbookName");
			oldSheetName = getParameterValue("oldSheetName");

			newSheetName = getParameterValue("newSheetName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((allCells == null) || (workbookName == null)
				|| (workbookName.equals("")) || (oldSheetName == null)
				|| (oldSheetName.equals("")) || (newSheetName == null)
				|| (newSheetName.equals("")))
			throw new RuntimeException("impossible to Rename the sheet");

		if (newSheetName.equals(oldSheetName))
			return _messageFactory.createStatus(EStatus.OKAY);

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(workbookName)
					&& cell.getWorksheet().equals(newSheetName )) {
				throw new RuntimeException("impossible to Rename the sheet: destination already exists");

			}

		}
		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(workbookName)
					&& cell.getWorksheet().equals(oldSheetName)) {
					_informationFlowModel.addName(cell, new CellName(workbookName, newSheetName, cell.getRow(), cell.getCol()));

					_informationFlowModel.removeName(cell,false);
			}

		}


		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
