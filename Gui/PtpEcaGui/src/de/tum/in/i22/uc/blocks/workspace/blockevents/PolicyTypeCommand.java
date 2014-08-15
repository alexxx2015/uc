package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.Param;
import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * 27 April 2013
 * This class handles undo and redo of change of policy
 * type values
 * 
 * @author ELIJAH
 *  
 */
public class PolicyTypeCommand implements Command {
	
	private Param param;
	private String sOldValue;
	private String sNewValue;
	private RenderableBlock rbParent;
	
	/**
	 * 
	 * @param prevState
	 */
	public PolicyTypeCommand(PreviousState prevState){
		rbParent=(RenderableBlock) prevState.getState();
		param=(Param)prevState.getAlOtherStates().get(0);
		sNewValue=(String)prevState.getAlOtherStates().get(1);
		sOldValue=(String)prevState.getAlOtherStates().get(2);		
	}

	@Override
	public void execute() {
		

	}

	//Revert to old value
	@Override
	public void undo() {
		
		param=rbParent.getParam();
		param.setPolicyType(sOldValue);
	}

	//Set our new value again
	@Override
	public void redo() {
		
		param=rbParent.getParam();
		param.setPolicyType(sNewValue);
	}

}
