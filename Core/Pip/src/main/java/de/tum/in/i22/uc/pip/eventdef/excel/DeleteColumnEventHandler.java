package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class DeleteColumnEventHandler extends ExcelEvents {

	public DeleteColumnEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		int colNumber = -1;
		String workbookName = "";
		String sheetName = "";
		Collection<CellName> allCells = _informationFlowModel.getAllNames(CellName.class);

		try {
			colNumber = Integer.valueOf(getParameterValue("colNumber"));
			workbookName = getParameterValue("workbookName");
			sheetName = getParameterValue("sheetName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((allCells == null) || (workbookName == null)
				|| (workbookName.equals("")) || (sheetName == null)
				|| (sheetName.equals("")))
			throw new RuntimeException(
					"impossible to delete Column with empty target");

		// I know this allocates a lot of space for no reason but I couldn't
		// come up with something better quickly
		// PLEASE FIXME
		Set<CellName>[] higherColNum = new HashSet[65536];
		int maxCol = 0;

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(workbookName)
					&& cell.getWorksheet().equals(sheetName)) {
				if (cell.getCol() == colNumber) {
					_informationFlowModel.remove(_informationFlowModel
							.getContainer(cell));
				} else if (cell.getCol() > colNumber) {
					// if the cell has a higher colnumber than the one we
					// delete, we store it
					if (higherColNum[cell.getCol()] == null)
						higherColNum[cell.getCol()] = new HashSet<CellName>();
					higherColNum[cell.getCol()].add(cell);
					maxCol = Math.max(maxCol, cell.getCol());
				}
				// otherwise nothing needs to be done
			}
		}

		// for every column after the one we deleted, we need to shift back the
		// names of one column. i.e. f[col-1 <-- f(col)]
		for (int col = colNumber + 1; col <= maxCol; col++) {
			if (higherColNum[col] != null) {
				for (CellName cell : higherColNum[col]) {
					IContainer cont = _informationFlowModel.getContainer(cell);
					_informationFlowModel.addName(
							new CellName(cell.getWorkbook(), cell
									.getWorksheet(), cell.getRow(), cell
									.getCol() - 1), cont, true);
					// here the container should never be removed anyway because
					// it has another name (we just created it), but just in
					// case we set the boolean to false
					_informationFlowModel.removeName(cell, false);
				}
			}
		}
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
