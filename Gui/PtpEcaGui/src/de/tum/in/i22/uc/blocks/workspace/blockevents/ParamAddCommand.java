package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles addition of parameters during policy
 * specification 
 * 
 * @author ELIJAH
 *
 */
public class ParamAddCommand implements Command {

	private RenderableBlock rbBlock;
	private BlockReceiverAction blockReceiverAction;		
	
	/**
	 * Creates an instance keeping copy of
	 * block
	 * 
	 * @param block
	 */
	public ParamAddCommand(RenderableBlock block){
		rbBlock=block;
		blockReceiverAction=new BlockReceiverAction();				
	}

	@Override
	public void execute() {
		

	}

	@Override
	public void undo() {
		
		blockReceiverAction.removeParam(rbBlock);
	}

	@Override
	public void redo() {
		
		blockReceiverAction.createParam(rbBlock,null);
	}

}
