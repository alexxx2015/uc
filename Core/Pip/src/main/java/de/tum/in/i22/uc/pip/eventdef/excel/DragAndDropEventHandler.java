package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class DragAndDropEventHandler extends ExcelEvents {

	public DragAndDropEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		String srcWorkbookName = "";
		String srcSheetName = "";
		String destWorkbookName = "";
		String destSheetName = "";
		String srcCoordinate = "";
		int rowCount = -1;
		int colCount = -1;
		int rowDiff = -1;
		int colDiff = -1;
		

		Collection<CellName> allCells = _informationFlowModel.getAllNames(CellName.class);

		try {
			rowCount = Integer.valueOf(getParameterValue("RowCount"));
			colCount = Integer.valueOf(getParameterValue("ColCount"));
			srcWorkbookName = getParameterValue("srcWorkbookName");
			srcSheetName = getParameterValue("srcSheetName");
			srcCoordinate = getParameterValue("srcCoordinate");
			rowDiff = Integer.valueOf(getParameterValue("RowDiff"));
			colDiff = Integer.valueOf(getParameterValue("ColDiff"));
			destWorkbookName = getParameterValue("destWorkbookName");
			destSheetName = getParameterValue("destSheetName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((allCells == null) 
				|| (srcCoordinate == null) 
				|| (srcCoordinate.equals("")) 
				|| (srcWorkbookName == null) 
				|| (srcWorkbookName.equals("")) 
				|| (destWorkbookName == null) 
				|| (destWorkbookName.equals("")) 
				|| (srcSheetName == null)
				|| (srcSheetName.equals(""))
				|| (rowCount==-1)
				|| (colCount==-1)
				|| (rowDiff==-1)
				|| (colDiff==-1)
				)
			throw new RuntimeException(
					"impossible to delete row with empty target");

/*		// PLEASE FIXME
		TreeSet<CellName>[] higherRowNum = new HashSet[65536];
		int maxRow = 0;

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(srcSheetName)) {
				if (cell.getRow() == rowNumber) {
					_informationFlowModel.remove(_informationFlowModel
							.getContainer(cell));
				} else if (cell.getRow() > rowNumber) {
					// if the cell has a higher colnumber than the one we
					// delete, we store it
					if (higherRowNum[cell.getRow()] == null)
						higherRowNum[cell.getRow()] = new HashSet<CellName>();
					higherRowNum[cell.getRow()].add(cell);
					maxRow = Math.max(maxRow, cell.getRow());
				}
				// otherwise nothing needs to be done
			}
		}

		// for every column after the one we deleted, we need to shift back the
		// names of one column. i.e. f[col-1 <-- f(col)]
		for (int row = rowNumber + 1; row <= maxRow; row++) {
			if (higherRowNum[row] != null) {
				for (CellName cell : higherRowNum[row]) {
					IContainer cont = _informationFlowModel.getContainer(cell);
					_informationFlowModel.addName(
							new CellName(cell.getWorkbook(), cell
									.getWorksheet(), cell.getRow() - 1, cell
									.getCol()), cont, true);
					// here the container should never be removed anyway because
					// it has another name (we just created it), but just in
					// case we set the boolean to false
					_informationFlowModel.removeName(cell, false);
				}
			}
		}
*/		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
