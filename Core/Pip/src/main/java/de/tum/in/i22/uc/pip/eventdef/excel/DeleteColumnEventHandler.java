package de.tum.in.i22.uc.pip.eventdef.excel;

import java.util.HashSet;
import java.util.Set;

import de.tum.in.i22.uc.cm.datatypes.basic.ContainerBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.NameBasic;
import de.tum.in.i22.uc.cm.datatypes.basic.StatusBasic.EStatus;
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
		int colNum = -1;
		String target = "";		
		try {
			colNum = Integer.getInteger(getParameterValue("ColNumber"));
			target = getParameterValue("Target");
			
		} catch (ParameterNotFoundException e) {
			_logger.error(e.getMessage());
			return _messageFactory.createStatus(
					EStatus.ERROR_EVENT_PARAMETER_MISSING, e.getMessage());
		}
		if ((target==null)||(target.equals(""))) throw new RuntimeException("impossible to delete Column with empty target");
		
		String[] coordinates = target.split(cs);
		
		Set<IName> currentSheet = cellsOnThisSheet(coordinates[0], coordinates[1]);
		
		Set<IName> higherColNum = new HashSet<IName>();
		int maxCol=0;
		
		for (IName cell : currentSheet){
			if (matchColumn(cell, colNum)){
				_informationFlowModel.removeName(cell);
			}
			//if the cell has a highe colnumber than the one we delete
			int curCol=Integer.getInteger(cell.getName().split(cs)[3]);
			maxCol=Math.max(maxCol, curCol); 
			if (curCol>colNum){
				higherColNum.add(cell);
			}
			//otherwise nothing needs to be done
		}
		
		//get highest column
		for (int col=colNum; col<=maxCol; col++){
			for (IName cell : higherColNum){
				if (matchColumn(cell, col)){
					//shift back the names of one column. i.e. f[col-1 <- f(col); col<-0]
					String[] coor = cell.getName().split(cs);
					IContainer cont = _informationFlowModel.getContainer(cell);
					_informationFlowModel.addName(new NameBasic(coor[0]+cs+coor[1]+cs+coor[2]+cs+(Integer.getInteger(coor[3])-1)), cont);
					_informationFlowModel.removeName(cell);
				}
			}
		}
		return _messageFactory.createStatus(EStatus.OKAY);
	}

}
