package de.tum.in.i22.uc.blocks.workspace.blockevents;

import javax.swing.DefaultCellEditor;

import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.model.ParamTableModel;

/**
 * Handles change of value of parameter status. Recall every
 * parameter has 4 parts: status (checkbox), name (combo-box),
 * class (combo-box) and value (textbox) 
 * 
 * @author ELIJAH
 * 
 */
public class ParamStatusChangeCommand implements Command {

	//Parent block of parameter
	private RenderableBlock rbParent;
	//Row value of parameter
	private int iRow;
	//Column value of parameter
	private int iColumn;	
	//Parameter table model
	private ParamTableModel tableModel;
	
	public ParamStatusChangeCommand(PreviousState state){
		rbParent=(RenderableBlock)state.getState();
		iRow=(Integer)state.getAlOtherStates().get(0);		
		iColumn=(Integer)state.getAlOtherStates().get(1);		
	}
	
	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		_do();
	}

	@Override
	public void redo() {
		
		_do();
	}
	
	private void _do(){
		Param param=rbParent.getParam();		
		tableModel=(ParamTableModel)param.getParamTable().getModel();
		boolean b=(Boolean)tableModel.getValueAt(iRow, iColumn);
		tableModel.setValueAt(!b, iRow, 0,false);
		prepareValueComponent(param,!b,iRow,2);
		prepareValueComponent(param,!b,iRow,3);		
	}
	
	//Sets the parameter value state just as the state of the parameter.
	/**
	 * Sets the state of other cell editors along this parameter
	 * row as the state of the status checkbox.
	 * 
	 * @param param
	 * @param status
	 * @param row
	 * @param col
	 */
	private void prepareValueComponent(Param param, boolean status, int row, int col){		
		DefaultCellEditor dce=(DefaultCellEditor)param.getParamTable().getCellEditor(row, col);		
		dce.getComponent().setEnabled(status);			
	}

}
