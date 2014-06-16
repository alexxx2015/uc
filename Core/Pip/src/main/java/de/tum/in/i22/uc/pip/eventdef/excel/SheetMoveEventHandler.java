package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class SheetMoveEventHandler extends ExcelEvents {

	public SheetMoveEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String srcWorkbookName = "";
		String sheetName = "";
		String destWorkbookName = "";
		

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

		try {

			srcWorkbookName = getParameterValue("srcWorkbookName");
			sheetName = getParameterValue("sheetName");
			destWorkbookName = getParameterValue("destWorkbookName");
		

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((allCells == null) || (srcWorkbookName == null)
				|| (srcWorkbookName.equals("")) || (sheetName == null)
				|| (sheetName.equals("")) || (destWorkbookName.equals("")))
			throw new RuntimeException("impossible to Move  the sheet");

		

		/*for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(sheetName )) {
				throw new RuntimeException("impossible to move the sheet: destination already contains exists");

			}

		}*/
		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(sheetName)) {
					_informationFlowModel.addName(cell, new CellName(destWorkbookName, sheetName, cell.getRow(), cell.getCol()));
					
					//_informationFlowModel.removeName(cell,false);
			}

		}

		
		return _messageFactory.createStatus(EStatus.OKAY);

	}

}
