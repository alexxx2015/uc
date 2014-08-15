package de.tum.in.i22.uc.blocks.workspace.blockevents;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;

import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.model.ParamTableModel;

/**
 * Handles events of parameter changes which include
 * policy-type, status, class and value values.
 * 
 * Deprecated because its complicated
 * 
 * @author ELIJAH
 *
 */
public class ParamChangeCommand implements Command {
	
	//private BlockReceiverAction receiverAction;
	private RenderableBlock rbParent;
	private int iRow;
	private int iColumn;
	private String sChangeType;
	private String sOldParameterValue;
	private String sNewParameterValue;
	private ParamTableModel tableModel; 
	
	public ParamChangeCommand(PreviousState ps){
		//receiverAction=new BlockReceiverAction();
		rbParent=(RenderableBlock)ps.getState();
		iRow=(Integer)ps.getAlOtherStates().get(0);
		sChangeType=(String)ps.getAlOtherStates().get(1);
		if(!isParamStatus()){
			iColumn=(Integer)ps.getAlOtherStates().get(2);
			sOldParameterValue=(String)ps.getAlOtherStates().get(3);
			sNewParameterValue=(String)ps.getAlOtherStates().get(4);
		}
	}
	
	private boolean isParamStatus(){
		if(sChangeType.equalsIgnoreCase("status")) return true;
		else return false;
	}

	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		//undo or redo should not fire events again
		Param param=rbParent.getParam();		
		tableModel=(ParamTableModel)param.getParamTable().getModel();
		
		if(isParamStatus()){			
			boolean b=(Boolean)tableModel.getValueAt(iRow, 0);
			tableModel.setValueAt(!b, iRow, 0,false);
			prepareValueComponent(param,!b,iRow,2);
			prepareValueComponent(param,!b,iRow,3);
		}
		else{
			DefaultCellEditor dce=(DefaultCellEditor)param.getParamTable().getCellEditor(iRow, iColumn);
			//JComboBox jcb=(JComboBox)dce.getComponent();
			//jcb.setEnabled(true);
			dce.getComponent().setEnabled(true);
			tableModel.setValueAt(sOldParameterValue, iRow, iColumn,false);
			if((dce.getComponent() instanceof JComboBox) && sOldParameterValue.equals(" ")){				
				((JComboBox)dce.getComponent()).setSelectedIndex(-1);
			}
		}
	}

	@Override
	public void redo() {
		
		//undo or redo should not fire events again
		Param param=rbParent.getParam();		
		tableModel=(ParamTableModel)param.getParamTable().getModel();
		
		if(isParamStatus()){
			boolean b=(Boolean)tableModel.getValueAt(iRow, 0);
			tableModel.setValueAt(!b, iRow, 0,false);			
			prepareValueComponent(param,!b,iRow,2);
			prepareValueComponent(param,!b,iRow,3);
		}
		else{
			DefaultCellEditor dce=(DefaultCellEditor)param.getParamTable().getCellEditor(iRow, iColumn);
			//JComboBox jcb=(JComboBox)dce.getComponent();
			//jcb.setEnabled(true);
			dce.getComponent().setEnabled(true);
			tableModel.setValueAt(sNewParameterValue, iRow, iColumn,false);
			if((dce.getComponent() instanceof JComboBox) && sNewParameterValue.equals(" ")){				
				((JComboBox)dce.getComponent()).setSelectedIndex(-1);
			}
		}
	}
	
	//Sets the parameter value state just as the state of the parameter.
	private void prepareValueComponent(Param p, boolean b, int row, int col){
		DefaultCellEditor dce=(DefaultCellEditor)p.getParamTable().getCellEditor(row, col);
		//JComboBox jcb=(JComboBox)dce.getComponent();
		tableModel=(ParamTableModel)p.getParamTable().getModel();
		if(b) dce.getComponent().setEnabled(true);
		else{
			//tableModel.setValueAt(" ", row, col,false);
			//jcb.setSelectedIndex(-1);
			dce.getComponent().setEnabled(false);
		}
		
	}

}
