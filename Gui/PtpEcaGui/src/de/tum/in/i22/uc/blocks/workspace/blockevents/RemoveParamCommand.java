package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;
import de.tum.in.i22.uc.model.ParamTableModel;

/**
 * Handles parameter remove event
 * 
 * @author ELIJAH
 *
 */
public class RemoveParamCommand implements Command {

	private RenderableBlock rbBlock;
	private BlockReceiverAction blockReceiverAction;
	private ParamTableModel tableModel;	
	
	/**
	 * 
	 * @param state
	 */
	public RemoveParamCommand(PreviousState state){
		rbBlock=(RenderableBlock)state.getState();
		blockReceiverAction=new BlockReceiverAction();		
		tableModel=(ParamTableModel)state.getAlOtherStates().get(0);
	}

	@Override
	public void execute() {
		

	}

	//undo means adding the param back
	@Override
	public void undo() {
		
		//set block genus name first				
		blockReceiverAction.createParam(rbBlock,tableModel);
	}

	@Override
	public void redo() {
		
		blockReceiverAction.removeParam(rbBlock);
	}
}
