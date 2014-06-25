package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SheetCopyEventHandler extends ExcelEvents {

	public SheetCopyEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String srcWorkbookName = "";
		String oldSheetName = "";
		String destWorkbookName = "";
		String newSheetName = "";

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

		try {

			srcWorkbookName = getParameterValue("srcWorkbookName");
			oldSheetName = getParameterValue("oldSheetName");
			destWorkbookName = getParameterValue("destWorkbookName");
			newSheetName = getParameterValue("newSheetName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((allCells == null) || (srcWorkbookName == null)
				|| (srcWorkbookName.equals("")) || (oldSheetName == null)
				|| (oldSheetName.equals("")) || (newSheetName == null)
				|| (newSheetName.equals(""))|| (destWorkbookName.equals("")))
			throw new RuntimeException("impossible to Create a copy of the  the sheet");

		if (newSheetName.equals(oldSheetName))
			return _messageFactory.createStatus(EStatus.OKAY);

		/*for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(newSheetName )) {
				throw new RuntimeException("impossible to copy the sheet: destination already contains exists");

			}

		}*/
		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(oldSheetName)) {
					_informationFlowModel.addName(cell, new CellName(destWorkbookName, newSheetName, cell.getRow(), cell.getCol()));
					
				//	_informationFlowModel.removeName(cell,false);
			}

		}

		
		return _messageFactory.createStatus(EStatus.OKAY);

	}

}
