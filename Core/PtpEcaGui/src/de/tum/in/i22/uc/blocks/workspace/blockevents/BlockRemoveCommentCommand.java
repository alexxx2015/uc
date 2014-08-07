package de.tum.in.i22.uc.blocks.workspace.blockevents;

import de.tum.in.i22.uc.blocks.renderable.RenderableBlock;

/**
 * Handles block comment removal events
 * 
 * @author ELIJAH
 *
 */
public class BlockRemoveCommentCommand implements Command {

	private RenderableBlock rbBlock;
	private BlockReceiverAction blockReceiverAction;
	private String sComment;
	
	/**
	 * 
	 * 
	 * @param state
	 */
	public BlockRemoveCommentCommand(PreviousState state){
		rbBlock=(RenderableBlock)state.getState();
		blockReceiverAction=new BlockReceiverAction();		
		sComment=(String)state.getAlOtherStates().get(0);
	}

	@Override
	public void execute() {

	}

	//undo means adding the comment back
	@Override
	public void undo() {
		blockReceiverAction.createComment(rbBlock,sComment);
	}

	@Override
	public void redo() {
		blockReceiverAction.removeComment(rbBlock);
	}

}
