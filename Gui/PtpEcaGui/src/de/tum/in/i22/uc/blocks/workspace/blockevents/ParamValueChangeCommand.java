package de.tum.in.i22.uc.blocks.workspace.blockevents;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.model.ParamTableModel;

/**
 * Handles change of value of parameter value. Recall every
 * parameter has 4 parts: status (checkbox), name (combo-box),
 * class (combo-box) and value (textbox) 
 * 
 * @author ELIJAH
 */
public class ParamValueChangeCommand implements Command {
	
	//Parent block of parameter
	private RenderableBlock rbParent;
	//Row value of parameter
	private int iRow;
	//Column value of parameter
	private int iColumn;
	//Old value of class parameter
	private String sOldValue;
	//New value of class parameter
	private String sNewValue;
	//Parameter table model
	private ParamTableModel tableModel;
	
	/**
	 * 
	 * @param state
	 */
	public ParamValueChangeCommand(PreviousState state){
		rbParent=(RenderableBlock)state.getState();
		iRow=(Integer)state.getAlOtherStates().get(0);		
		iColumn=(Integer)state.getAlOtherStates().get(1);
		sOldValue=(String)state.getAlOtherStates().get(2);
		sNewValue=(String)state.getAlOtherStates().get(3);	
	}
		
	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		Param param=rbParent.getParam();		
		tableModel=(ParamTableModel)param.getParamTable().getModel();	
		DefaultCellEditor dce=(DefaultCellEditor)param.getParamTable().getCellEditor(iRow, iColumn);		
		dce.getComponent().setEnabled(true);
		tableModel.setValueAt(sOldValue, iRow, iColumn,false);
		if((dce.getComponent() instanceof JComboBox) && sOldValue.equals(" ")){				
			((JComboBox)dce.getComponent()).setSelectedIndex(-1);
		}
	}

	@Override
	public void redo() {
		
		Param param=rbParent.getParam();		
		tableModel=(ParamTableModel)param.getParamTable().getModel();
		DefaultCellEditor dce=(DefaultCellEditor)param.getParamTable().getCellEditor(iRow, iColumn);		
		dce.getComponent().setEnabled(true);
		tableModel.setValueAt(sNewValue, iRow, iColumn,false);
		if((dce.getComponent() instanceof JComboBox) && sNewValue.equals(" ")){				
			((JComboBox)dce.getComponent()).setSelectedIndex(-1);
		}
	}

}
