package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
import de.tum.in.i22.uc.cm.datatypes.excel.CellName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IContainer;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IName;
import de.tum.in.i22.uc.cm.datatypes.interfaces.IStatus;
import de.tum.in.i22.uc.pip.eventdef.ParameterNotFoundException;

public class DeleteRowEventHandler extends ExcelEvents {

	public DeleteRowEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		int rowNumber = -1;
		String workbookName = "";
		String sheetName = "";
		try {
			rowNumber = Integer.valueOf(getParameterValue("RowNumber"));
			workbookName= getParameterValue("workbookName");
			sheetName= getParameterValue("sheetName");

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((workbookName == null) || (workbookName.equals(""))|| (sheetName == null) || (sheetName.equals("")))
			throw new RuntimeException(
					"impossible to delete Rowumn with empty target");

		Collection<CellName> allCells = _informationFlowModel
				.getAllNames(CellName.class);

		Set<CellName> higherRowNum = new HashSet<CellName>();
		int maxRow = 0;

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(workbookName)
					&& cell.getWorksheet().equals(sheetName)){
				if (cell.getRow() == rowNumber) {
					_informationFlowModel.removeName(cell);
				} else if (cell.getRow()>rowNumber){
					// if the cell has a higher rownumber than the one we delete
					higherRowNum.add(cell);
					maxRow = Math.max(maxRow, cell.getRow());
				}
				// otherwise nothing needs to be done
			}
		}

		
		// for every row  after the one we deleted, we need to shift back the names of one row. i.e. f[row-1 <-
		// f(row); row<-0]
		
		for (int row = rowNumber+1; row <= maxRow; row++) {
			for (CellName cell : higherRowNum) {
				if (cell.getRow()==row) {
					IContainer cont = _informationFlowModel.getContainer(cell);
					_informationFlowModel.addName(
							new CellName(cell.getWorkbook(), cell.getWorksheet(),cell.getRow(),cell.getRow() - 1),
							cont);
					_informationFlowModel.removeName(cell);
				}
			}
		}
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
