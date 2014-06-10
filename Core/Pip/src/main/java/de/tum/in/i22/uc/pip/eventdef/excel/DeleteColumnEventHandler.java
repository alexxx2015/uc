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

public class DeleteColumnEventHandler extends ExcelEvents {

	public DeleteColumnEventHandler() {
		super();
	}

	@Override
	protected IStatus update() {
		int colNumber = -1;
		String workbookName = "";
		String sheetName = "";
		Collection<CellName> allCells;
		try {
			colNumber = Integer.valueOf(getParameterValue("ColNumber"));
			workbookName= getParameterValue("workbookName");
			sheetName= getParameterValue("sheetName");

			allCells = _informationFlowModel
					.getAllNames(CellName.class);

		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		} 
		if ((allCells==null)||(workbookName == null) || (workbookName.equals(""))|| (sheetName == null) || (sheetName.equals("")))
			throw new RuntimeException(
					"impossible to delete Column with empty target");


		Set<CellName> higherColNum = new HashSet<CellName>();
		int maxCol = 0;

		for (CellName cell : allCells) {
			if (cell.getWorkbook().equals(workbookName)
					&& cell.getWorksheet().equals(sheetName)){
				if (cell.getCol() == colNumber) {
					_informationFlowModel.remove(_informationFlowModel.getContainer(cell));
				} else if (cell.getCol()>colNumber){
					// if the cell has a higher colnumber than the one we delete, we store it
					higherColNum.add(cell);
					maxCol = Math.max(maxCol, cell.getCol());
				}
				// otherwise nothing needs to be done
			}
		}

		
		// for every column after the one we deleted, we need to shift back the names of one column. i.e. f[col-1 <-
		// f(col); col<-0]
		
		for (int col = colNumber+1; col <= maxCol; col++) {
			for (CellName cell : higherColNum) {
				if (cell.getCol()==col) {
					IContainer cont = _informationFlowModel.getContainer(cell);
					_informationFlowModel.addName(
							new CellName(cell.getWorkbook(), cell.getWorksheet(),cell.getRow(),cell.getCol() - 1),
							cont);
					_informationFlowModel.removeName(cell);
				}
			}
		}
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
