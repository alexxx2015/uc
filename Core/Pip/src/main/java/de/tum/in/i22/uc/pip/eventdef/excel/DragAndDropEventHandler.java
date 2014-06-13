package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.core.ifm.BasicInformationFlowModel.SortByNames;
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

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

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
		if ((allCells == null) || (srcCoordinate == null)
				|| (srcCoordinate.equals("")) || (srcWorkbookName == null)
				|| (srcWorkbookName.equals("")) || (destWorkbookName == null)
				|| (destWorkbookName.equals("")) || (srcSheetName == null)
				|| (srcSheetName.equals("")) || (destSheetName == null)
				|| (destSheetName.equals("")) || (rowCount == -1)
				|| (colCount == -1) || (rowDiff == -1) || (colDiff == -1))
			throw new RuntimeException(
					"impossible to delete row with empty target");

		Set<CellName> srcSet = new HashSet<CellName>();
		Set<CellName> dstSet = new HashSet<CellName>();
		int maxCol = 0;

		CellName src = new CellName(srcCoordinate);

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(srcSheetName)) {
				if ((cell.getRow() >= src.getRow())
						&& (cell.getRow() <= src.getRow() + rowCount)
						&& (cell.getCol() >= src.getCol())
						&& (cell.getCol() <= src.getCol() + colCount)) {
					srcSet.add(cell);
				}

			}
		}

		TreeSet<CellName> srcColSortedSet;
		TreeSet<CellName> srcRowSortedSet;

		if (colDiff < 0) {
			srcColSortedSet = new TreeSet<CellName>(new SortByCol());
		} else {
			srcColSortedSet = new TreeSet<CellName>(new SortByColReverse());
		}
		srcColSortedSet.addAll(srcSet);

		if (rowDiff < 0) {
			srcRowSortedSet = new TreeSet<CellName>(new SortByRow());
		} else {
			srcRowSortedSet = new TreeSet<CellName>(new SortByRowReverse());
		}

		
		//populate srcRowSortedSet

		
//		for (int i=)
		
		
		
//		// for every column after the one we deleted, we need to shift back the
//		// names of one column. i.e. f[col-1 <-- f(col)]
//		for (int row = rowNumber + 1; row <= maxRow; row++) {
//			if (srcSet[row] != null) {
//				for (CellName cell : srcSet[row]) {
//					IContainer cont = _informationFlowModel.getContainer(cell);
//					_informationFlowModel.addName(
//							new CellName(cell.getWorkbook(), cell
//									.getWorksheet(), cell.getRow() - 1, cell
//									.getCol()), cont, true);
//					// here the container should never be removed anyway because
//					// it has another name (we just created it), but just in
//					// case we set the boolean to false
//					_informationFlowModel.removeName(cell, false);
//				}
//			}
//		}
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
