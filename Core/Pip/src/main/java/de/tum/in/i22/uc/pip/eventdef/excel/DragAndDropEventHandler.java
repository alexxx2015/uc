package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.TreeSet;

import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
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

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

		try {
			rowCount = Integer.valueOf(getParameterValue("rowCount"));
			colCount = Integer.valueOf(getParameterValue("colCount"));
			srcWorkbookName = getParameterValue("srcWorkbookName");
			srcSheetName = getParameterValue("srcSheetName");
			srcCoordinate = getParameterValue("srcCoordinate");
			rowDiff = Integer.valueOf(getParameterValue("rowDiff"));
			colDiff = Integer.valueOf(getParameterValue("colDiff"));
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

		if (colDiff > 0) {
			SortCellName.setRevertColSort(false);
		} else {
			SortCellName.setRevertColSort(true);
		}

		if (rowDiff > 0) {
			SortCellName.setRevertRowSort(false);
		} else {
			SortCellName.setRevertRowSort(true);
		}

		TreeSet<CellName> srcSetSorted = new TreeSet<CellName>(
				new SortCellName());

		CellName src = CellName.create(srcCoordinate);

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(srcWorkbookName)
					&& cell.getWorksheet().equals(srcSheetName)) {
				if ((cell.getRow() >= src.getRow())
						&& (cell.getRow() < src.getRow() + rowCount)
						&& (cell.getCol() >= src.getCol())
						&& (cell.getCol() < src.getCol() + colCount)) {
					srcSetSorted.add(cell);
				}

			}
		}

		for (CellName c : srcSetSorted) {
			_informationFlowModel.addName(c, new CellName(destWorkbookName,
					destSheetName, c.getRow() + rowDiff, c.getCol() + colDiff));
			_informationFlowModel.removeName(c,false);
		}

		//
		// // populate srcRowSortedSet
		//
		// Iterator<CellName> itr = srcColSortedSet.iterator();
		// int currCol = -1;
		// CellName currCell;
		// if (itr.hasNext()) {
		// srcRowSortedSet.add(itr.next());
		// while (itr.hasNext()) {
		// currCell = itr.next();
		// while (currCell.getCol() == currCol) {
		// srcRowSortedSet.add(currCell);
		// currCell = itr.next();
		// }
		// for (CellName cn : srcRowSortedSet){
		// _informationFlowModel.addName(cn,
		// new CellName(destWorkbookName, destSheetName, cn.getRow()+rowDiff, cn
		// .getCol() + colDiff));
		// // here the container should never be removed anyway because
		// // it has another name (we just created it), but just in
		// // case we set the boolean to false
		// _informationFlowModel.removeName(cn, false);
		//
		// }
		//
		// srcRowSortedSet.clear();
		// srcRowSortedSet.add(currCell);
		// }
		// }
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
